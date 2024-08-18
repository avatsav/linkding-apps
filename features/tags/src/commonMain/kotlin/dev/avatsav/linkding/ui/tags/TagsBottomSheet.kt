package dev.avatsav.linkding.ui.tags

import com.slack.circuit.foundation.CircuitContent
import com.slack.circuit.foundation.NavEvent
import com.slack.circuit.overlay.OverlayHost
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.ui.TagsScreen
import dev.avatsav.linkding.ui.TagsScreenResult
import dev.avatsav.linkding.ui.compose.circuit.BottomSheetOverlay

suspend fun OverlayHost.showTagsBottomSheet(selectedTags: List<Tag>): TagsScreenResult =
    show<TagsScreenResult>(
        BottomSheetOverlay(
            model = selectedTags,
            onDismiss = { TagsScreenResult.Dismissed },
        ) { data, overlayNavigator ->
            CircuitContent(
                screen = TagsScreen(data.map { it.mapToScreenParam() }),
                onNavEvent = { event ->
                    when (event) {
                        is NavEvent.Pop -> {
                            overlayNavigator.finish(
                                event.result as? TagsScreenResult ?: TagsScreenResult.Dismissed,
                            )
                        }

                        else ->
                            overlayNavigator.finish(TagsScreenResult.Dismissed)
                    }
                },
            )
        },
    )
