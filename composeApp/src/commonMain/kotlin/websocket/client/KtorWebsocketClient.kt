package websocket.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.js.Js
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.websocket.DefaultClientWebSocketSession
import io.ktor.client.plugins.websocket.WebSockets
import io.ktor.client.plugins.websocket.sendSerialized
import io.ktor.client.plugins.websocket.webSocketSession
import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.websocket.Frame
import io.ktor.websocket.close
import io.ktor.websocket.readText
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.serialization.json.Json
import pl.cube.shared.model.Task

private const val RECONNECT_DELAY = 10_000L

class KtorWebsocketClient(
    private val url: String,
    private val listener: WebsocketEvents,
) {
    private val client = HttpClient(Js) {
        install(Logging)
        install(WebSockets) {
            contentConverter = KotlinxWebsocketSerializationConverter(Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            })
        }
    }

    private val scope =
        CoroutineScope(Dispatchers.Default) + SupervisorJob() + CoroutineExceptionHandler { _, throwable ->
            log("Error: ${throwable.message}")
        }

    private var job: Job? = null

    private var session: DefaultClientWebSocketSession? = null

    suspend fun connect() {
        try {
            log("Connecting to websocket at $url...")

            session = client.webSocketSession(url)

            listener.onConnected()

            log("Connected to websocket at $url")

            session!!.incoming
                .receiveAsFlow()
                .filterNotNull()
                .collect { data ->
                    when (data) {
                        is Frame.Text -> {
                            val message = data.readText()
                            val task: Task = Json.decodeFromString(message)
                            listener.onReceive(task)

                            log("Received message: $message")
                        }

                        is Frame.Binary -> {
                            log("Received binary message")
                        }

                        is Frame.Close -> {
                            listener.onDisconnected("Received close message")
                            log("Received close message")
                        }

                        is Frame.Ping -> {
                            log("Received ping message")
                        }

                        is Frame.Pong -> {
                            log("Received pong message")
                        }

                        else -> {
                            log("Received unknown message")
                        }
                    }

                }
        } catch (e: Exception) {
            listener.onDisconnected(e.message ?: "Unknown error")

            log("Error: ${e.message}")

            reconnect()
        }
    }

    private fun reconnect() {
        job?.cancel()

        log("Reconnecting to websocket in $RECONNECT_DELAY ms...")

        job = scope.launch {
            stop()
            delay(RECONNECT_DELAY)
            connect()
        }
    }

    suspend fun stop() {
        log("Closing websocket session...")

        session?.close()
        session = null
    }

    internal suspend inline fun <reified T> send(message: T) {
        log("Sending message: $message")
        session?.sendSerialized(message)
    }

    fun log(message: String) {
        listener.log(message)
    }
}