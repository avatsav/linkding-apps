package dev.avatsav.linkding.bookmarks.ui.list.widgets

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import dev.avatsav.linkding.ui.compose.appearFromBottom
import dev.avatsav.linkding.ui.compose.disappearToBottom
import dev.avatsav.linkding.ui.compose.widgets.AnimatedVisibilityWithElevation

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
internal fun BookmarkActionsToolbar(
  visible: Boolean,
  modifier: Modifier = Modifier,
  content: @Composable RowScope.() -> Unit,
) {
  AnimatedVisibilityWithElevation(
    visible = visible,
    modifier = modifier,
    enter = appearFromBottom(),
    exit = disappearToBottom(),
  ) { elevation ->
    HorizontalFloatingToolbar(
      expanded = true,
      colors = FloatingToolbarDefaults.vibrantFloatingToolbarColors(),
      expandedShadowElevation = elevation,
      content = content,
    )
  }
}
