package dev.avatsav.linkding.bookmarks.ui.list.widgets

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import dev.avatsav.linkding.ui.compose.appearFromBottom
import dev.avatsav.linkding.ui.compose.disappearToBottom
import dev.avatsav.linkding.ui.compose.widgets.AnimatedVisibilityWithElevation

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun <T : Any> BookmarkActionsToolbar(
  target: T?,
  modifier: Modifier = Modifier,
  content: @Composable RowScope.(target: T, active: Boolean) -> Unit,
) {
  AnimatedVisibilityWithElevation(
    visible = target != null,
    modifier = modifier,
    enter = appearFromBottom(),
    exit = disappearToBottom(),
  ) { elevation ->
    // Keep the outgoing row at its original size until the slide and fade finish.
    var lastTarget by remember { mutableStateOf(target) }
    if (target != null) SideEffect { lastTarget = target }
    val displayedTarget = target ?: lastTarget ?: return@AnimatedVisibilityWithElevation
    HorizontalFloatingToolbar(
      expanded = true,
      colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(),
      expandedShadowElevation = elevation,
      content = { content(displayedTarget, target != null) },
    )
  }
}
