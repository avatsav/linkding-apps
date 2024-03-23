package dev.avatsav.linkding.ui.tags

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState

@Immutable
data class TagsUiState(
    val eventSink: (TagsUiEvent) -> Unit,
) : CircuitUiState

sealed interface TagsUiEvent : CircuitUiEvent
