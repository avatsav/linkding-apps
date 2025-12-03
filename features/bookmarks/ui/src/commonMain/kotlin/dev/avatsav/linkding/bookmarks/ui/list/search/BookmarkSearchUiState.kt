package dev.avatsav.linkding.bookmarks.ui.list.search

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import dev.avatsav.linkding.bookmarks.ui.list.common.SnackbarMessage
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.SearchHistory
import dev.avatsav.linkding.data.model.Tag

data class BookmarkSearchUiState(
  val query: String = "",
  val filters: BookmarkFiltersUiState = BookmarkFiltersUiState(),
  val results: LazyPagingItems<Bookmark>,
  val history: List<SearchHistory> = listOf(),
  val snackbarMessage: SnackbarMessage? = null,
) {

  fun isIdle(): Boolean = !isActive() && history.isNotEmpty()

  fun isActive(): Boolean =
    query.isNotBlank() ||
      filters.bookmarkCategory != BookmarkCategory.All ||
      filters.selectedTags.isNotEmpty()

  fun isLoading(): Boolean = isActive() && results.loadState.refresh is LoadState.Loading

  fun hasSearchResults(): Boolean =
    isActive() && results.loadState.refresh is LoadState.NotLoading && results.itemCount != 0

  fun hasNoSearchResults(): Boolean =
    isActive() && results.loadState.refresh is LoadState.NotLoading && results.itemCount == 0
}

data class BookmarkFiltersUiState(
  val bookmarkCategory: BookmarkCategory = BookmarkCategory.All,
  val selectedTags: List<Tag> = listOf(),
)
