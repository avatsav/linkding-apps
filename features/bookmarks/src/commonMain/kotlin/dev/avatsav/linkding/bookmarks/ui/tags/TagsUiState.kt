package dev.avatsav.linkding.bookmarks.ui.tags

import androidx.compose.runtime.Immutable
import androidx.paging.compose.LazyPagingItems
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import dev.avatsav.linkding.data.model.Tag

@Immutable
data class TagsUiState(
    val selectedTags: List<Tag>,
    val tags: LazyPagingItems<Tag>,
    val eventSink: (TagsUiEvent) -> Unit,
) : CircuitUiState

sealed interface TagsUiEvent : CircuitUiEvent {
    data class SelectTag(val tag: Tag) : TagsUiEvent
    data object Close : TagsUiEvent
}
