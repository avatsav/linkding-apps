package dev.avatsav.linkding.bookmarks.ui.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
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
import dev.avatsav.linkding.bookmarks.api.interactors.ClearSearchHistoryInteractor
import dev.avatsav.linkding.bookmarks.api.interactors.DeleteBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.SaveSearchStateInteractor
import dev.avatsav.linkding.bookmarks.api.interactors.UnarchiveBookmark
import dev.avatsav.linkding.bookmarks.api.observers.ObserveBookmarks
import dev.avatsav.linkding.bookmarks.api.observers.ObserveSearchHistory
import dev.avatsav.linkding.bookmarks.ui.list.common.PendingAction
import dev.avatsav.linkding.bookmarks.ui.list.common.rememberPendingBookmarkActionHandler
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.SearchHistory
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.presenter.MoleculePresenter
import dev.zacsweers.metro.Inject
import kotlin.time.Clock
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

@Inject
class BookmarksPresenter(
  private val observeBookmarks: ObserveBookmarks,
  private val observeSearchHistory: ObserveSearchHistory,
  private val saveSearchState: SaveSearchStateInteractor,
  private val clearSearchHistory: ClearSearchHistoryInteractor,
  private val deleteBookmark: DeleteBookmark,
  private val archiveBookmark: ArchiveBookmark,
  private val unarchiveBookmark: UnarchiveBookmark,
) : MoleculePresenter<BookmarksUiEvent, BookmarksUiState, BookmarkUiEffect>() {

  @Suppress("CyclomaticComplexMethod")
  @Composable
  override fun models(events: Flow<BookmarksUiEvent>): BookmarksUiState {
    var searchQuery by rememberSaveable { mutableStateOf("") }
    var bookmarkCategory by rememberSaveable { mutableStateOf(BookmarkCategory.All) }
    val selectedTags = remember { mutableStateListOf<Tag>() }
    val isSearchOrFilteringActive =
      searchQuery.isNotBlank() ||
        bookmarkCategory != BookmarkCategory.All ||
        selectedTags.isNotEmpty()

    val actionHandler =
      rememberPendingBookmarkActionHandler(
        scope = presenterScope,
        deleteBookmark = deleteBookmark,
        archiveBookmark = archiveBookmark,
        unarchiveBookmark = unarchiveBookmark,
        removePendingIdsOnSuccess = { !isSearchOrFilteringActive },
      )

    val baseBookmarksFlow: Flow<PagingData<Bookmark>> by
      produceState(
        initialValue = emptyFlow(),
        key1 = searchQuery,
        key2 = bookmarkCategory,
        key3 = selectedTags.toList(),
      ) {
        observeBookmarks(
          ObserveBookmarks.Param(
            cached = !isSearchOrFilteringActive, // Use cache for feed, API for search/filter
            query = searchQuery,
            category = bookmarkCategory,
            tags = selectedTags.toList(),
            pagingConfig = PagingConfig(initialLoadSize = 20, pageSize = 20),
          )
        )
        value = observeBookmarks.flow.cachedIn(presenterScope)
      }

    val bookmarksFlow: Flow<PagingData<Bookmark>> by
      produceState(emptyFlow(), key1 = baseBookmarksFlow) {
        value =
          combine(baseBookmarksFlow, snapshotFlow { actionHandler.pendingIds.toSet() }) {
            pagingData,
            pending ->
            pagingData.filter { bookmark -> bookmark.id !in pending }
          }
      }

    val bookmarks = bookmarksFlow.collectAsLazyPagingItems()

    val searchHistoryFlow: Flow<List<SearchHistory>> by
      produceState(emptyFlow()) {
        observeSearchHistory(ObserveSearchHistory.Params)
        value = observeSearchHistory.flow
      }

    val searchHistory by searchHistoryFlow.collectAsState(emptyList())

    ObserveEvents { event ->
      when (event) {
        AddBookmark -> emitEffect(BookmarkUiEffect.AddBookmark)
        ShowSettings -> emitEffect(BookmarkUiEffect.NavigateToSettings)

        is Search -> {
          searchQuery = event.query
          if (event.query.isNotBlank()) {
            presenterScope.launch {
              saveSearchState(
                param =
                  SaveSearchStateInteractor.Params(
                    SearchHistory(query = event.query, modified = Clock.System.now())
                  )
              )
            }
          }
          actionHandler.clearPendingIds()
        }

        is SelectCategory -> {
          bookmarkCategory = event.category
          actionHandler.clearPendingIds()
        }

        is SetTags -> {
          selectedTags.clear()
          selectedTags.addAll(event.tags)
          actionHandler.clearPendingIds()
        }

        is RemoveTag -> {
          selectedTags.remove(event.tag)
          actionHandler.clearPendingIds()
        }

        is SelectSearchHistory -> {
          searchQuery = event.item.query
          actionHandler.clearPendingIds()
        }

        ClearSearch -> {
          searchQuery = ""
          // Keep category and tags as per user preference
          actionHandler.clearPendingIds()
        }

        ClearSearchHistory -> {
          presenterScope.launch { clearSearchHistory(ClearSearchHistoryInteractor.Params) }
        }

        is Open -> emitEffect(BookmarkUiEffect.OpenBookmark(event.bookmark))

        is Edit -> {
          emitEffect(BookmarkUiEffect.EditBookmark(event.bookmark))
        }

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

        Refresh -> presenterScope.launch { bookmarks.refresh() }
        UndoAction -> actionHandler.undoAction()
        DismissSnackbar -> actionHandler.dismissSnackbar()
      }
    }

    return BookmarksUiState(
      bookmarks = bookmarks,
      query = searchQuery,
      category = bookmarkCategory,
      selectedTags = selectedTags.toList(),
      searchHistory = searchHistory,
      snackbarMessage = actionHandler.snackbarMessage,
    )
  }
}
