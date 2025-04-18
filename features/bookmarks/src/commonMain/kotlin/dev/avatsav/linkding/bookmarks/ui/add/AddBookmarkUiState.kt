package dev.avatsav.linkding.bookmarks.ui.add

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import dev.avatsav.linkding.data.model.CheckUrlResult

@Immutable
data class AddBookmarkUiState(
  val sharedUrl: String? = null,
  val alreadyBookmarked: Boolean = false,
  val checkingUrl: Boolean = false,
  val checkUrlResult: CheckUrlResult? = null,
  val saving: Boolean = false,
  val errorMessage: String? = null,
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

  data class CheckUrl(val url: String) : AddBookmarkUiEvent
}
