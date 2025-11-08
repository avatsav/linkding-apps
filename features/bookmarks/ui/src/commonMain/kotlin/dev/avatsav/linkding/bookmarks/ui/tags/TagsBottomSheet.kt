package dev.avatsav.linkding.bookmarks.ui.tags

import androidx.compose.material3.ExperimentalMaterial3Api
import com.slack.circuit.foundation.CircuitContent
import com.slack.circuit.foundation.NavEvent
import com.slack.circuit.overlay.OverlayHost
import com.slack.circuitx.overlays.BottomSheetOverlay
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.ui.TagsScreen
import dev.avatsav.linkding.ui.TagsScreenResult

@OptIn(ExperimentalMaterial3Api::class)
suspend fun OverlayHost.showTagsBottomSheet(selectedTags: List<Tag>): TagsScreenResult =
  show<TagsScreenResult>(
    BottomSheetOverlay(model = selectedTags, onDismiss = { TagsScreenResult.Dismissed }) {
      data,
      overlayNavigator ->
      CircuitContent(
        screen = TagsScreen(data.map { it.mapToScreenParam() }),
        onNavEvent = { event ->
          when (event) {
            is NavEvent.Pop -> {
              overlayNavigator.finish(
                event.result as? TagsScreenResult ?: TagsScreenResult.Dismissed
              )
            }

            else -> overlayNavigator.finish(TagsScreenResult.Dismissed)
          }
        },
      )
    }
  )
