package dev.avatsav.linkding.settings.ui.widgets

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoMode
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.ui.compose.onCondition

@Composable
fun PreferenceColumnScope.SwitchPreference(
  title: String,
  description: String?,
  shape: Shape,
  checked: Boolean,
  onCheckedChange: () -> Unit,
  modifier: Modifier = Modifier,
) {
  Preference(
    title = title,
    description = description,
    shape = shape,
    control = {
      Switch(
        checked = checked,
        onCheckedChange = { onCheckedChange() },
        thumbContent = {
          if (checked)
            Icon(
              imageVector = Icons.Filled.Check,
              contentDescription = null,
              modifier = Modifier.size(SwitchDefaults.IconSize),
            )
          else
            Icon(
              imageVector = Icons.Filled.Close,
              contentDescription = null,
              modifier = Modifier.size(SwitchDefaults.IconSize),
            )
        },
      )
    },
    modifier = modifier,
  )
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PreferenceColumnScope.ThemePreference(
  shape: Shape,
  selected: AppTheme,
  onSelect: (AppTheme) -> Unit,
  modifier: Modifier = Modifier,
) {
  val options = AppTheme.entries
  Preference(title = "Theme", shape = shape, modifier = modifier) {
    Row(horizontalArrangement = Arrangement.spacedBy(ButtonGroupDefaults.ConnectedSpaceBetween)) {
      options.forEachIndexed { index, theme ->
        ToggleButton(
          checked = theme == selected,
          onCheckedChange = { onSelect(theme) },
          modifier = Modifier.semantics { role = Role.RadioButton },
          shapes =
            when (index) {
              0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
              options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
              else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
            },
        ) {
          Icon(imageVector = theme.icon(), contentDescription = theme.name)
          if (theme == selected) {
            Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
            Text(theme.name)
          }
        }
      }
    }
  }
}

private const val PreferenceAnimationDuration = 220

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PreferenceColumnScope.Preference(
  title: String,
  shape: Shape,
  modifier: Modifier = Modifier,
  description: String? = null,
  clickable: Boolean = false,
  onClick: () -> Unit = {},
  control: (@Composable () -> Unit)? = null,
) {
  Surface(
    modifier =
      modifier.defaultMinSize(56.dp).clip(shape)
        .onCondition(clickable) { clickable { onClick() } },
    shape = shape,
    tonalElevation = 8.dp,
  ) {
    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
      Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
        Text(text = title, style = MaterialTheme.typography.labelLarge)

        if (description != null) {
          AnimatedContent(
            targetState = description,
            transitionSpec = {
              (fadeIn() + slideInHorizontally(tween(PreferenceAnimationDuration)) togetherWith
                (slideOutHorizontally(tween(PreferenceAnimationDuration)) { it / 2 } + fadeOut()))
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
  modifier: Modifier = Modifier,
  space: Dp = 2.dp,
  content: @Composable PreferenceColumnScope.() -> Unit,
) {
  Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(space)) {
    val scope = remember { PreferenceColumnScopeWrapper(this) }
    if (title != null) {
      PreferenceHeader(title)
    }
    scope.content()
  }
}

@Composable
private fun PreferenceHeader(title: String, modifier: Modifier = Modifier) {
  Surface(modifier = modifier.padding(8.dp)) {
    Text(
      text = title,
      style = MaterialTheme.typography.labelLarge,
      color = MaterialTheme.colorScheme.primary,
    )
  }
}

interface PreferenceColumnScope : ColumnScope

private class PreferenceColumnScopeWrapper(scope: ColumnScope) :
  PreferenceColumnScope, ColumnScope by scope

private fun AppTheme.icon() =
  when (this) {
    AppTheme.System -> Icons.Default.AutoMode
    AppTheme.Light -> Icons.Default.LightMode
    AppTheme.Dark -> Icons.Default.DarkMode
  }
