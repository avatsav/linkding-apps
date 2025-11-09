package dev.avatsav.linkding.bookmarks.ui.list.search

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.filter
import dev.avatsav.linkding.bookmarks.api.interactors.ArchiveBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.ClearSearchHistory as ClearSearchHistoryInteractor
import dev.avatsav.linkding.bookmarks.api.interactors.DeleteBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.SaveSearchState
import dev.avatsav.linkding.bookmarks.api.interactors.UnarchiveBookmark
import dev.avatsav.linkding.bookmarks.api.observers.ObserveSearchHistory
import dev.avatsav.linkding.bookmarks.api.observers.ObserveSearchResults
import dev.avatsav.linkding.bookmarks.ui.list.common.PendingAction
import dev.avatsav.linkding.bookmarks.ui.list.common.rememberPendingActionHandler
import dev.avatsav.linkding.bookmarks.ui.list.search.BookmarkSearchUiEvent.ClearSearch
import dev.avatsav.linkding.bookmarks.ui.list.search.BookmarkSearchUiEvent.ClearSearchHistory
import dev.avatsav.linkding.bookmarks.ui.list.search.BookmarkSearchUiEvent.Delete
import dev.avatsav.linkding.bookmarks.ui.list.search.BookmarkSearchUiEvent.Open
import dev.avatsav.linkding.bookmarks.ui.list.search.BookmarkSearchUiEvent.RemoveTag
import dev.avatsav.linkding.bookmarks.ui.list.search.BookmarkSearchUiEvent.Search
import dev.avatsav.linkding.bookmarks.ui.list.search.BookmarkSearchUiEvent.SelectBookmarkCategory
import dev.avatsav.linkding.bookmarks.ui.list.search.BookmarkSearchUiEvent.SelectSearchHistoryItem
import dev.avatsav.linkding.bookmarks.ui.list.search.BookmarkSearchUiEvent.SelectTag
import dev.avatsav.linkding.bookmarks.ui.list.search.BookmarkSearchUiEvent.ToggleArchive
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.SearchHistory
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.viewmodel.MoleculePresenter
import kotlin.time.Clock
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

class BookmarkSearchPresenter(
  scope: CoroutineScope,
  private val observeSearchResults: ObserveSearchResults,
  private val observeSearchHistory: ObserveSearchHistory,
  private val saveSearchState: SaveSearchState,
  private val clearSearchHistory: ClearSearchHistoryInteractor,
  private val deleteBookmark: DeleteBookmark,
  private val archiveBookmark: ArchiveBookmark,
  private val unarchiveBookmark: UnarchiveBookmark,
  private val navigator: BookmarkSearchNavigator,
) : MoleculePresenter<BookmarkSearchUiEvent, BookmarkSearchUiState>(scope) {

  private val presenterScope = scope

  @Suppress("CyclomaticComplexMethod")
  @Composable
  override fun models(events: Flow<BookmarkSearchUiEvent>): BookmarkSearchUiState {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var bookmarkCategory by rememberSaveable { mutableStateOf(BookmarkCategory.All) }
    val selectedTags = remember { mutableStateListOf<Tag>() }

    val actionHandler =
      rememberPendingActionHandler(
        scope = presenterScope,
        deleteBookmark = deleteBookmark,
        archiveBookmark = archiveBookmark,
        unarchiveBookmark = unarchiveBookmark,
        // onSuccess: no-op, keep items hidden in search results
      )

    var basePagingFlow by remember { mutableStateOf<Flow<PagingData<Bookmark>>>(emptyFlow()) }

    LaunchedEffect(searchQuery, bookmarkCategory, selectedTags.size) {
      observeSearchResults(
        ObserveSearchResults.Param(
          query = searchQuery,
          category = bookmarkCategory,
          tags = selectedTags,
          pagingConfig = PagingConfig(initialLoadSize = 20, pageSize = 20),
        )
      )
      basePagingFlow = observeSearchResults.flow.cachedIn(presenterScope)
    }

    // Combine paging data with pendingIds changes to filter locally without reloading
    var searchResultsFlow by remember { mutableStateOf<Flow<PagingData<Bookmark>>>(emptyFlow()) }

    LaunchedEffect(basePagingFlow) {
      searchResultsFlow =
        combine(basePagingFlow, snapshotFlow { actionHandler.pendingIds.toSet() }) {
          pagingData,
          pending ->
          pagingData.filter { bookmark -> bookmark.id !in pending }
        }
    }

    val searchResults = searchResultsFlow.collectAsLazyPagingItems()

    var searchHistoryFlow by remember { mutableStateOf<Flow<List<SearchHistory>>>(emptyFlow()) }
    LaunchedEffect(Unit) {
      observeSearchHistory(ObserveSearchHistory.Params)
      searchHistoryFlow = observeSearchHistory.flow
    }
    val searchHistory by searchHistoryFlow.collectAsState(initial = emptyList())

    CollectEvents { event ->
      when (event) {
        is ToggleArchive -> {
          val action =
            if (event.bookmark.archived) {
              PendingAction.Unarchive(bookmarkId = event.bookmark.id, bookmark = event.bookmark)
            } else {
              PendingAction.Archive(bookmarkId = event.bookmark.id, bookmark = event.bookmark)
            }
          actionHandler.scheduleAction(action)
        }

        is Delete -> {
          actionHandler.scheduleAction(
            PendingAction.Delete(bookmarkId = event.bookmark.id, bookmark = event.bookmark)
          )
        }

        is Open -> navigator.openUrl(event.bookmark.url)

        is BookmarkSearchUiEvent.Edit -> {
          // Editing bookmarks not yet implemented
        }

        is SelectBookmarkCategory -> {
          bookmarkCategory = event.category
          actionHandler.clearPendingIds()
        }

        is RemoveTag -> {
          selectedTags.remove(event.tag)
          actionHandler.clearPendingIds()
        }

        is SelectTag -> {
          if (!selectedTags.contains(event.tag)) {
            selectedTags.add(0, event.tag)
            actionHandler.clearPendingIds()
          }
        }

        is Search -> {
          searchQuery = event.query
          if (event.query.isNotBlank()) {
            presenterScope.launch {
              saveSearchState(
                param =
                  SaveSearchState.Params(
                    SearchHistory(
                      query = event.query,
                      bookmarkCategory = bookmarkCategory,
                      selectedTags = selectedTags,
                      timestamp = Clock.System.now(),
                    )
                  )
              )
            }
          }
          actionHandler.clearPendingIds()
        }

        is SelectSearchHistoryItem -> {
          searchQuery = event.item.query
          bookmarkCategory = event.item.bookmarkCategory
          selectedTags.clear()
          selectedTags.addAll(event.item.selectedTags)
          actionHandler.clearPendingIds()
        }

        ClearSearch -> {
          searchQuery = ""
          bookmarkCategory = BookmarkCategory.All
          selectedTags.clear()
          actionHandler.clearPendingIds()
        }

        ClearSearchHistory -> {
          presenterScope.launch { clearSearchHistory(ClearSearchHistoryInteractor.Params) }
        }

        BookmarkSearchUiEvent.UndoAction -> {
          actionHandler.undoAction()
        }

        BookmarkSearchUiEvent.DismissSnackbar -> {
          actionHandler.dismissSnackbar()
        }
      }
    }

    return BookmarkSearchUiState(
      query = searchQuery,
      results = searchResults,
      history = searchHistory,
      snackbarMessage = actionHandler.snackbarMessage,
      filters =
        BookmarkFiltersUiState(bookmarkCategory = bookmarkCategory, selectedTags = selectedTags),
    )
  }
}

interface BookmarkSearchNavigator {
  fun openUrl(url: String)
}
