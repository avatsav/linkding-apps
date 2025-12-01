package dev.avatsav.linkding.bookmarks.ui.list

import dev.avatsav.linkding.bookmarks.ui.list.feed.BookmarkFeedUiState
import dev.avatsav.linkding.bookmarks.ui.list.search.BookmarkSearchUiState
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.SearchHistory
import dev.avatsav.linkding.data.model.Tag

data class BookmarksUiState(
  val feedState: BookmarkFeedUiState,
  val searchState: BookmarkSearchUiState,
)

sealed interface BookmarksUiEvent {

  data object AddBookmark : BookmarksUiEvent

  data object ShowSettings : BookmarksUiEvent
}

sealed interface BookmarkFeedUiEvent : BookmarksUiEvent {
  data object Refresh : BookmarkFeedUiEvent

  data class ToggleArchive(val bookmark: Bookmark) : BookmarkFeedUiEvent

  data class Delete(val bookmark: Bookmark) : BookmarkFeedUiEvent

  data class Edit(val bookmark: Bookmark) : BookmarkFeedUiEvent

  data class Open(val bookmark: Bookmark) : BookmarkFeedUiEvent

  data object UndoAction : BookmarkFeedUiEvent

  data object DismissSnackbar : BookmarkFeedUiEvent
}

sealed interface BookmarkSearchUiEvent : BookmarksUiEvent {
  data class Search(val query: String) : BookmarkSearchUiEvent

  data class SelectBookmarkCategory(val category: BookmarkCategory) : BookmarkSearchUiEvent

  data class SelectTag(val tag: Tag) : BookmarkSearchUiEvent

  data class RemoveTag(val tag: Tag) : BookmarkSearchUiEvent

  data class Open(val bookmark: Bookmark) : BookmarkSearchUiEvent

  data class Edit(val bookmark: Bookmark) : BookmarkSearchUiEvent

  data class ToggleArchive(val bookmark: Bookmark) : BookmarkSearchUiEvent

  data class Delete(val bookmark: Bookmark) : BookmarkSearchUiEvent

  data class SelectSearchHistoryItem(val item: SearchHistory) : BookmarkSearchUiEvent

  data object ClearSearch : BookmarkSearchUiEvent

  data object ClearSearchHistory : BookmarkSearchUiEvent

  data object UndoAction : BookmarkSearchUiEvent

  data object DismissSnackbar : BookmarkSearchUiEvent
}

sealed interface BookmarkUiEffect {
  data class OpenBookmark(val bookmark: Bookmark) : BookmarkUiEffect

  data object AddBookmark : BookmarkUiEffect

  data object NavigateToSettings : BookmarkUiEffect
}
