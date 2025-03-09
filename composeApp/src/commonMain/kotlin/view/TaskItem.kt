package view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlintaskerproject.composeapp.generated.resources.Res
import kotlintaskerproject.composeapp.generated.resources.task_item_description
import kotlintaskerproject.composeapp.generated.resources.task_item_name
import org.jetbrains.compose.resources.stringResource
import pl.cube.shared.model.Priority

@Composable
internal fun TasksItem(
    name: String,
    description: String,
    priority: Priority,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .fillMaxWidth()
            .padding(4.dp),
    ) {
        Icon(
            imageVector = priority.icon(),
            contentDescription = priority.name,
            modifier = Modifier.padding(16.dp),
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
        )
        Column {
            Text(
                text = stringResource(Res.string.task_item_name, name),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(
                text = stringResource(Res.string.task_item_description, description),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
        }
    }
}
