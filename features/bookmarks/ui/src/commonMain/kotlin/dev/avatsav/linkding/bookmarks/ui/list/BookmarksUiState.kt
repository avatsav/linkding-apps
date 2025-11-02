package dev.avatsav.linkding.bookmarks.ui.list

import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import dev.avatsav.linkding.bookmarks.ui.list.feed.BookmarkListUiState
import dev.avatsav.linkding.bookmarks.ui.list.search.BookmarkSearchUiState

data class BookmarksUiState(
  val feedState: BookmarkListUiState,
  val searchState: BookmarkSearchUiState,
  val eventSink: (BookmarksUiEvent) -> Unit,
) : CircuitUiState

sealed interface BookmarksUiEvent : CircuitUiEvent {
  data object AddBookmark : BookmarksUiEvent

  data object ShowSettings : BookmarksUiEvent
}
