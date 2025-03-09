package viewmodel

import androidx.compose.runtime.Stable
import pl.cube.shared.model.Task

@Stable
data class TasksUiState(
    val isConnected: Boolean = false,
    val tasks: List<Task> = emptyList(),
    val log: String = "",
)
