package dev.avatsav.linkding.ui.tags

import com.slack.circuit.foundation.CircuitContent
import com.slack.circuit.foundation.NavEvent
import com.slack.circuit.overlay.OverlayHost
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.ui.TagsScreen
import dev.avatsav.linkding.ui.compose.widgets.BottomSheetOverlay

suspend fun OverlayHost.showTagsBottomSheet(selectedTags: List<Tag>): TagsScreen.Result {
    return show<TagsScreen.Result>(
        BottomSheetOverlay(
            model = selectedTags,
            onDismiss = { TagsScreen.Result.Dismissed },
        ) { data, overlayNavigator ->
            CircuitContent(
                screen = TagsScreen(data.map { it.name }),
                onNavEvent = { event ->
                    when (event) {
                        is NavEvent.Pop -> {
                            overlayNavigator.finish(
                                event.result as? TagsScreen.Result ?: TagsScreen.Result.Dismissed,
                            )
                        }

                        else ->
                            overlayNavigator.finish(TagsScreen.Result.Dismissed)
                    }
                },
            )
        },
    )
}
