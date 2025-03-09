package viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import pl.cube.shared.model.Priority
import pl.cube.shared.model.Task
import websocket.client.KtorWebsocketClient
import websocket.client.WebsocketEvents

private const val WS_URL = "ws://127.0.0.1:8080/tasks"

internal class TasksViewModel : ViewModel(), WebsocketEvents {

    private val client = KtorWebsocketClient(WS_URL, this)

    private val _uiState = MutableStateFlow(TasksUiState())
    val uiState: StateFlow<TasksUiState> = _uiState.asStateFlow()

    internal fun onConnectClick() {
        viewModelScope.launch {
            _uiState.update { it.copy(tasks = emptyList()) }
            client.connect()
        }
    }

    internal fun onDisconnectClick() {
        viewModelScope.launch {
            client.stop()
        }
    }

    internal fun onAddTaskClick(name: String, description: String, priority: Priority) {
        val task = Task(name, description, priority)
        viewModelScope.launch {
            client.send(task)
        }
    }

    override fun <T> onReceive(data: T) {
        if (data is Task) {
            _uiState.update { it.copy(tasks = it.tasks + data) }
        }
    }

    override fun onConnected() {
        _uiState.update { it.copy(isConnected = true) }
    }

    override fun onDisconnected(reason: String) {
        _uiState.update { it.copy(isConnected = false) }
        log("disconnected: $reason")
    }

    override fun log(message: String) {
        updateLog(message)
    }

    private fun updateLog(log: String) {
        _uiState.update { it.copy(log = it.log + "\n" + log) }
    }
}