package dev.avatsav.linkding.bookmarks.ui.list.feed

import androidx.paging.compose.LazyPagingItems
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import dev.avatsav.linkding.bookmarks.ui.list.common.SnackbarMessage
import dev.avatsav.linkding.data.model.Bookmark

data class BookmarkListUiState(
  val bookmarks: LazyPagingItems<Bookmark>,
  val snackbarMessage: SnackbarMessage? = null,
  val eventSink: (BookmarkListUiEvent) -> Unit,
) : CircuitUiState

sealed interface BookmarkListUiEvent : CircuitUiEvent {
  data object Refresh : BookmarkListUiEvent

  data class ToggleArchive(val bookmark: Bookmark) : BookmarkListUiEvent

  data class Delete(val bookmark: Bookmark) : BookmarkListUiEvent

  data class Edit(val bookmark: Bookmark) : BookmarkListUiEvent

  data class Open(val bookmark: Bookmark) : BookmarkListUiEvent

  data object UndoAction : BookmarkListUiEvent

  data object DismissSnackbar : BookmarkListUiEvent
}
