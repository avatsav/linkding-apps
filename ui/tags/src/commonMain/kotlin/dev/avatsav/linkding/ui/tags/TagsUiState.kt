package dev.avatsav.linkding.ui.tags

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import kotlinx.collections.immutable.ImmutableList

@Immutable
data class TagsUiState(
    val selectedTags: List<String>,
    val tags: ImmutableList<String>,
    val eventSink: (TagsUiEvent) -> Unit,
) : CircuitUiState

sealed interface TagsUiEvent : CircuitUiEvent {
    data class SelectTag(val tag: String) : TagsUiEvent
    data object Close : TagsUiEvent
}
