package dev.avatsav.linkding.bookmarks.ui.list

import androidx.paging.compose.LazyPagingItems
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.SearchHistory
import dev.avatsav.linkding.data.model.Tag
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class BookmarksUiState(
  val bookmarkList: BookmarkListUiState,
  val search: SearchUiState,
  val eventSink: (BookmarksUiEvent) -> Unit,
) : CircuitUiState

data class BookmarkListUiState(val bookmarks: LazyPagingItems<Bookmark>)

data class SearchUiState(
  val history: ImmutableList<SearchHistory> = persistentListOf(),
  val query: String = "",
  val filters: BookmarkFiltersUiState = BookmarkFiltersUiState(),
  val results: LazyPagingItems<Bookmark>,
) {

  fun isIdle(): Boolean = !isActive() && history.isNotEmpty()

  fun isActive(): Boolean =
    query.isNotBlank() ||
      filters.bookmarkCategory != BookmarkCategory.All ||
      filters.selectedTags.isNotEmpty()

  fun isLoading(): Boolean =
    isActive() && results.loadState.refresh is androidx.paging.LoadState.Loading

  fun hasSearchResults(): Boolean =
    isActive() &&
      results.loadState.refresh is androidx.paging.LoadState.NotLoading &&
      results.itemCount != 0

  fun hasNoSearchResults(): Boolean =
    isActive() &&
      results.loadState.refresh is androidx.paging.LoadState.NotLoading &&
      results.itemCount == 0
}

data class BookmarkFiltersUiState(
  val bookmarkCategory: BookmarkCategory = BookmarkCategory.All,
  val selectedTags: ImmutableList<Tag> = persistentListOf(),
)

sealed interface BookmarksUiEvent : CircuitUiEvent {
  data object Refresh : BookmarksUiEvent

  data class ToggleArchive(val bookmark: Bookmark) : BookmarksUiEvent
  data class Delete(val bookmark: Bookmark) : BookmarksUiEvent

  data class Edit(val bookmark: Bookmark) : BookmarksUiEvent
  data class Open(val bookmark: Bookmark) : BookmarksUiEvent

  data class SelectBookmarkCategory(val bookmarkCategory: BookmarkCategory) : BookmarksUiEvent

  data object AddBookmark : BookmarksUiEvent

  data object ShowSettings : BookmarksUiEvent

  data class SelectTag(val tag: Tag) : BookmarksUiEvent

  data class RemoveTag(val tag: Tag) : BookmarksUiEvent

  data class Search(val query: String) : BookmarksUiEvent

  data object ClearSearch : BookmarksUiEvent

  data class SelectSearchHistoryItem(val searchHistory: SearchHistory) : BookmarksUiEvent

  data object ClearSearchHistory : BookmarksUiEvent
}
