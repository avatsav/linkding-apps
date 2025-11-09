package dev.avatsav.linkding.bookmarks.ui.list

import dev.avatsav.linkding.bookmarks.ui.list.feed.BookmarkListUiState
import dev.avatsav.linkding.bookmarks.ui.list.search.BookmarkSearchUiState

data class BookmarksUiState(
  val feedState: BookmarkListUiState,
  val searchState: BookmarkSearchUiState,
)

sealed interface BookmarksUiEvent {
  data object AddBookmark : BookmarksUiEvent

  data object ShowSettings : BookmarksUiEvent
}
