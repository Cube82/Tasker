package view

import androidx.compose.runtime.Composable
import kotlintaskerproject.composeapp.generated.resources.*
import org.jetbrains.compose.resources.vectorResource
import pl.cube.shared.model.Priority
import pl.cube.shared.model.Priority.*

@Composable
fun Priority.icon() = when (this) {
    Low -> vectorResource(Res.drawable.ic_priority_low)
    Medium -> vectorResource(Res.drawable.ic_priority_medium)
    High -> vectorResource(Res.drawable.ic_priority_high)
    Vital -> vectorResource(Res.drawable.ic_priority_vital)
}