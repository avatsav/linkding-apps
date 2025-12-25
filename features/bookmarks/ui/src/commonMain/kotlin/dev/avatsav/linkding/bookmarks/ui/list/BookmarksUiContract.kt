package dev.avatsav.linkding.bookmarks.ui.list

import androidx.paging.compose.LazyPagingItems
import dev.avatsav.linkding.bookmarks.ui.list.common.SnackbarMessage
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.SearchHistory
import dev.avatsav.linkding.data.model.Tag

data class BookmarksUiState(
  val bookmarks: LazyPagingItems<Bookmark>,
  val query: String = "",
  val category: BookmarkCategory = BookmarkCategory.All,
  val selectedTags: List<Tag> = emptyList(),
  val searchHistory: List<SearchHistory> = emptyList(),
  val snackbarMessage: SnackbarMessage? = null,
) {
  val isSearchActive: Boolean
    get() = query.isNotBlank() || category != BookmarkCategory.All || selectedTags.isNotEmpty()

  val filteredHistory: List<SearchHistory>
    get() =
      if (query.isBlank()) searchHistory
      else searchHistory.filter { it.query.contains(query, ignoreCase = true) }
}

sealed interface BookmarksUiEvent {
  // Navigation
  data object AddBookmark : BookmarksUiEvent

  data object ShowSettings : BookmarksUiEvent

  // Search & Filters
  data class Search(val query: String) : BookmarksUiEvent

  data class SelectCategory(val category: BookmarkCategory) : BookmarksUiEvent

  data class SetTags(val tags: List<Tag>) : BookmarksUiEvent

  data class RemoveTag(val tag: Tag) : BookmarksUiEvent

  data class SelectSearchHistory(val item: SearchHistory) : BookmarksUiEvent

  data object ClearSearch : BookmarksUiEvent // Clears query only, keeps category/tags

  data object ClearSearchHistory : BookmarksUiEvent

  // Bookmark actions
  data class Open(val bookmark: Bookmark) : BookmarksUiEvent

  data class Edit(val bookmark: Bookmark) : BookmarksUiEvent

  data class ToggleArchive(val bookmark: Bookmark) : BookmarksUiEvent

  data class Delete(val bookmark: Bookmark) : BookmarksUiEvent

  // Refresh & Undo
  data object Refresh : BookmarksUiEvent

  data object UndoAction : BookmarksUiEvent

  data object DismissSnackbar : BookmarksUiEvent
}

sealed interface BookmarkUiEffect {
  data class OpenBookmark(val bookmark: Bookmark) : BookmarkUiEffect

  data class EditBookmark(val bookmark: Bookmark) : BookmarkUiEffect

  data object AddBookmark : BookmarkUiEffect

  data object NavigateToSettings : BookmarkUiEffect
}
