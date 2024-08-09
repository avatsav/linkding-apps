package dev.avatsav.linkding.ui.compose.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Constraints

@Composable
fun OffsetStatusBar(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val statusBarHeight = WindowInsets.statusBars.getTop(LocalDensity.current)
    Box(
        modifier = modifier.layout { measurable, constraints ->
            val newConstraints = Constraints(
                minWidth = constraints.minWidth,
                maxWidth = constraints.maxWidth,
                minHeight = constraints.minHeight,
                maxHeight = when (constraints.maxHeight) {
                    Constraints.Infinity -> Constraints.Infinity
                    else -> constraints.maxHeight - statusBarHeight
                },
            )
            val placeable = measurable.measure(newConstraints)

            layout(placeable.width, placeable.height) {
                placeable.placeRelative(0, 0)
            }
        },
    ) {
        content()
    }
}
