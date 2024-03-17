package dev.avatsav.linkding.ui.settings.widget

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.data.model.prefs.AppTheme

@Composable
fun PreferenceColumnScope.SwitchPreference(
    title: String,
    description: String? = null,
    shape: Shape,
    modifier: Modifier = Modifier,
    checked: Boolean,
    onCheckedChange: () -> Unit,
) {
    Preference(
        title = title,
        description = description,
        shape = shape,
        control = {
            Switch(
                checked = checked,
                onCheckedChange = { onCheckedChange() },
            )
        },
        modifier = modifier,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferenceColumnScope.ThemePreference(
    shape: Shape,
    selected: AppTheme,
    onSelected: (AppTheme) -> Unit,
) {
    val options = AppTheme.entries
    Preference(
        title = "Theme",
        description = selected.name,
        shape = shape,
    ) {
        SingleChoiceSegmentedButtonRow {
            options.forEachIndexed { index, theme ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(
                        index = index,
                        count = options.size,
                    ),
                    onClick = { onSelected(theme) },
                    icon = {},
                    selected = theme == selected,
                    colors = SegmentedButtonDefaults.colors().copy(
                        activeContainerColor = MaterialTheme.colorScheme.primary,
                        activeContentColor = MaterialTheme.colorScheme.onPrimary,
                        activeBorderColor = MaterialTheme.colorScheme.primary,
                        inactiveBorderColor = MaterialTheme.colorScheme.outline,
                    ),
                ) {
                    Icon(
                        imageVector = when (theme) {
                            AppTheme.System -> Icons.Default.AutoMode
                            AppTheme.Light -> Icons.Default.LightMode
                            AppTheme.Dark -> Icons.Default.DarkMode
                        },
                        contentDescription = theme.name,
                    )
                }
            }
        }
    }
}

@Composable
fun PreferenceColumnScope.Preference(
    title: String,
    description: String? = null,
    shape: Shape,
    modifier: Modifier = Modifier,
    control: (@Composable () -> Unit)? = null,
) {
    Surface(
        modifier = modifier,
        shape = shape,
        color = MaterialTheme.colorScheme.surfaceColorAtElevation(8.dp),
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp),
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge,
                )

                if (description != null) {
                    AnimatedContent(
                        targetState = description,
                        transitionSpec = {
                            (fadeIn() + slideInHorizontally(tween(220)))
                                .togetherWith(
                                    slideOutHorizontally(
                                        tween(220),
                                        targetOffsetX = { it / 2 },
                                    ) + fadeOut(),
                                )
                        },
                    ) { target ->
                        Text(
                            text = target,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            control?.invoke()
        }
    }
}

@Composable
fun PreferenceSection(
    title: String?,
    space: Dp = PreferenceDefaults.SectionWidth,
    modifier: Modifier = Modifier,
    content: @Composable PreferenceColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(space),
    ) {
        val scope = remember { PreferenceColumnScopeWrapper(this) }
        if (title != null) {
            PreferenceHeader(title)
            Spacer(Modifier.height(4.dp))
        }
        scope.content()
    }
}

@Composable
private fun PreferenceHeader(
    title: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.padding(horizontal = 16.dp, vertical = 4.dp),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

interface PreferenceColumnScope : ColumnScope

private class PreferenceColumnScopeWrapper(scope: ColumnScope) :
    PreferenceColumnScope,
    ColumnScope by scope

internal object PreferenceDefaults {
    val SectionWidth = 2.dp

    val baseShape: CornerBasedShape
        get() = RoundedCornerShape(4.dp)

    fun itemShape(index: Int, count: Int, baseShape: CornerBasedShape = this.baseShape): Shape {
        if (count == 1) return baseShape
        return when (index) {
            0 -> baseShape.start()
            count - 1 -> baseShape.end()
            else -> RectangleShape
        }
    }
}

private fun CornerBasedShape.start(): CornerBasedShape {
    return copy(topStart = CornerSize(28.dp), topEnd = CornerSize(28.dp))
}

private fun CornerBasedShape.end(): CornerBasedShape {
    return copy(bottomStart = CornerSize(28.dp), bottomEnd = CornerSize(28.dp))
}
