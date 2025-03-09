package view

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material3.MenuAnchorType.Companion.PrimaryNotEditable
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.ktor.http.parseUrl
import kotlintaskerproject.composeapp.generated.resources.*
import kotlinx.browser.window
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import pl.cube.shared.model.Priority
import ui.darkScheme
import ui.lightScheme
import viewmodel.TasksUiState
import viewmodel.TasksViewModel

private const val DEBUG_MODE_PARAM = "debug"
private const val DEBUG_MODE_VALUE = "true"

@Composable
internal fun TasksScreen(viewModel: TasksViewModel = koinInject()) {
    val state = viewModel.uiState.collectAsState().value
    TasksView(
        state = state,
        onConnectClick = viewModel::onConnectClick,
        onDisconnectClick = viewModel::onDisconnectClick,
        onAddTaskClick = viewModel::onAddTaskClick,
    )
}

@Composable
private fun TasksView(
    state: TasksUiState,
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit,
    onAddTaskClick: (String, String, Priority) -> Unit,
) {
    val scheme = if (isSystemInDarkTheme()) darkScheme else lightScheme
    val debugMode =
        parseUrl(window.location.href)?.parameters?.get(DEBUG_MODE_PARAM) == DEBUG_MODE_VALUE
    MaterialTheme(
        colorScheme = scheme,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 16.dp),
            contentAlignment = Alignment.Center,
        ) {
            Row {
                Column(
                    modifier = Modifier
                        .fillMaxHeight()
                        .verticalScroll(rememberScrollState())
                        .width(512.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Spacer(Modifier.padding(16.dp))
                    ConnectSection(onConnectClick, onDisconnectClick)
                    NewTaskSection(onAddTaskClick, state.isConnected)

                    Column(
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        state.tasks.forEach { task ->
                            TasksItem(
                                name = task.name,
                                description = task.description,
                                priority = task.priority,
                            )
                        }
                    }

                    Spacer(Modifier.padding(16.dp))
                }
                if (debugMode) {
                    DebugSection(state.log)
                }
            }
        }
    }
}

@Composable
private fun ConnectSection(
    onConnectClick: () -> Unit,
    onDisconnectClick: () -> Unit,
) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Button(
            onClick = { onConnectClick.invoke() },
        ) {
            Text(stringResource(Res.string.button_connect))
        }
        Button(
            onClick = { onDisconnectClick.invoke() },
        ) {
            Text(stringResource(Res.string.button_disconnect))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun NewTaskSection(
    onAddTaskClick: (String, String, Priority) -> Unit,
    isEnabled: Boolean,
) {
    var showMenu by remember { mutableStateOf(false) }
    var taskName by remember { mutableStateOf("") }
    var isTaskNameError by remember { mutableStateOf(false) }
    var taskDescription by remember { mutableStateOf("") }
    var isTaskDescriptionError by remember { mutableStateOf(false) }
    var taskPriority by remember { mutableStateOf(Priority.Medium) }

    OutlinedTextField(
        value = taskName,
        onValueChange = {
            taskName = it
            isTaskNameError = false
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(Res.string.label_task_name)) },
        isError = isTaskNameError,
        enabled = isEnabled,
        singleLine = true,
    )

    OutlinedTextField(
        value = taskDescription,
        onValueChange = {
            taskDescription = it
            isTaskDescriptionError = false
        },
        modifier = Modifier.fillMaxWidth(),
        label = { Text(stringResource(Res.string.label_task_description)) },
        isError = isTaskDescriptionError,
        enabled = isEnabled,
        singleLine = true,
    )

    ExposedDropdownMenuBox(
        expanded = showMenu,
        onExpandedChange = { showMenu = it },
        modifier = Modifier.fillMaxWidth(),
    ) {
        OutlinedTextField(
            modifier = Modifier.menuAnchor(PrimaryNotEditable).fillMaxWidth(),
            value = taskPriority.name,
            onValueChange = {},
            enabled = isEnabled,
            readOnly = true,
            label = { Text(stringResource(Res.string.label_task_priority)) },
            singleLine = true,
        )
        ExposedDropdownMenu(
            expanded = showMenu && isEnabled,
            onDismissRequest = { showMenu = false },
        ) {
            Priority.entries.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option.name) },
                    leadingIcon = {
                        Icon(
                            imageVector = option.icon(),
                            contentDescription = option.name,
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    },
                    onClick = {
                        taskPriority = option
                        showMenu = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }

    Button(
        onClick = {
            if (taskName.isNotBlank() && taskDescription.isNotBlank()) {
                onAddTaskClick.invoke(taskName.trim(), taskDescription.trim(), taskPriority)
                taskName = ""
                taskDescription = ""
                taskPriority = Priority.Medium
            } else {
                isTaskNameError = taskName.isBlank()
                isTaskDescriptionError = taskDescription.isBlank()
            }
        },
        enabled = isEnabled,
    ) {
        Text(stringResource(Res.string.button_add_task))
    }
}

@Composable
private fun DebugSection(
    log: String,
) {
    Text(
        text = log,
        modifier = Modifier.padding(horizontal = 16.dp),
        fontSize = 10.sp,
        color = MaterialTheme.colorScheme.onBackground,
    )
}
