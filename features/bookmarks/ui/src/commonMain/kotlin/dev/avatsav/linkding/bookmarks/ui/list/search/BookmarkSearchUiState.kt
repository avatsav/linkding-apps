package dev.avatsav.linkding.bookmarks.ui.list.search

import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import dev.avatsav.linkding.bookmarks.ui.list.common.SnackbarMessage
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.SearchHistory
import dev.avatsav.linkding.data.model.Tag
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.persistentListOf

data class BookmarkSearchUiState(
  val query: String = "",
  val filters: BookmarkFiltersUiState = BookmarkFiltersUiState(),
  val results: LazyPagingItems<Bookmark>,
  val history: ImmutableList<SearchHistory> = persistentListOf(),
  val snackbarMessage: SnackbarMessage? = null,
  val eventSink: (BookmarkSearchUiEvent) -> Unit,
) : CircuitUiState {

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
  val selectedTags: ImmutableList<Tag> = persistentListOf(),
)

sealed interface BookmarkSearchUiEvent : CircuitUiEvent {
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
