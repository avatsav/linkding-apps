package dev.avatsav.linkding.ui.add

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import dev.avatsav.linkding.data.model.UnfurlData

@Immutable
data class AddBookmarkUiState(
    val sharedUrl: String? = null,
    val unfurling: Boolean = false,
    val unfurlData: UnfurlData? = null,
    val saving: Boolean = false,
    val saveErrorMessage: String? = null,
    val eventSink: (AddBookmarkUiEvent) -> Unit,
) : CircuitUiState

sealed interface AddBookmarkUiEvent : CircuitUiEvent {
    data class Save(
        val url: String,
        val title: String?,
        val description: String?,
        val tags: List<String>,
    ) : AddBookmarkUiEvent

    data object Close : AddBookmarkUiEvent
}

