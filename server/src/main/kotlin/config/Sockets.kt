package pl.cube.config

import io.ktor.serialization.kotlinx.KotlinxWebsocketSerializationConverter
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.routing.routing
import io.ktor.server.websocket.*
import io.ktor.websocket.close
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.channels.ClosedReceiveChannelException
import kotlinx.coroutines.delay
import kotlinx.serialization.json.Json
import pl.cube.TaskRepositoryImpl
import pl.cube.shared.model.Task
import java.util.Collections
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration.Companion.seconds

@OptIn(DelicateCoroutinesApi::class)
fun Application.configureSockets(repository: TaskRepositoryImpl) {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
        pingPeriod = 15.seconds
        timeout = 15.seconds
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        val sessions = Collections.synchronizedList<WebSocketServerSession>(ArrayList())

        webSocket("/tasks") {
            sessions.add(this)
            sendAllTasks(repository)

            while (true) {
                val newTask = receiveDeserialized<Task>()
                repository.add(newTask)
                for (session in sessions) {
                    try {
                        if (!session.outgoing.isClosedForSend) {
                            session.sendSerialized(newTask)
                        }
                    } catch (e: Exception) {
                        when (e) {
                            is ClosedReceiveChannelException, is CancellationException -> {
                                println("Failed to send message: Connection closed.")
                                if (!session.outgoing.isClosedForSend) {
                                    session.close()
                                }
                            }

                            else -> println("An unexpected error occurred: ${e.message}")
                        }
                    }
                }
                sessions.removeAll {
                    it.outgoing.isClosedForSend
                }
            }
        }
    }
}

private suspend fun DefaultWebSocketServerSession.sendAllTasks(repository: TaskRepositoryImpl) {
    for (task in repository.allTasks()) {
        sendSerialized(task)
        delay(200)
    }
}