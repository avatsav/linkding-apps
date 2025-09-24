package dev.avatsav.linkding.bookmarks.ui.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.paging.PagingConfig
import androidx.paging.cachedIn
import androidx.paging.compose.collectAsLazyPagingItems
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.avatsav.linkding.bookmarks.api.interactors.ArchiveBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.ClearSearchHistory as ClearSearchHistoryInteractor
import dev.avatsav.linkding.bookmarks.api.interactors.DeleteBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.SaveSearchState
import dev.avatsav.linkding.bookmarks.api.interactors.UnarchiveBookmark
import dev.avatsav.linkding.bookmarks.api.observers.ObserveBookmarks
import dev.avatsav.linkding.bookmarks.api.observers.ObserveSearchHistory
import dev.avatsav.linkding.bookmarks.api.observers.ObserveSearchResults
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.AddBookmark
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.ClearSearch
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.ClearSearchHistory
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.Delete
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.Open
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.Refresh
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.RemoveTag
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.Search
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.SelectBookmarkCategory
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.SelectSearchHistoryItem
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.SelectTag
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.ShowSettings
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.ToggleArchive
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.SearchHistory
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.inject.UserScope
import dev.avatsav.linkding.ui.AddBookmarkScreen
import dev.avatsav.linkding.ui.BookmarksScreen
import dev.avatsav.linkding.ui.SettingsScreen
import dev.avatsav.linkding.ui.UrlScreen
import dev.avatsav.linkding.ui.circuit.produceRetainedState
import dev.avatsav.linkding.ui.circuit.rememberRetainedCoroutineScope
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.Inject
import kotlin.time.Clock
import kotlinx.collections.immutable.toImmutableList
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.launch

@Inject
@Suppress("LongParameterList")
class BookmarksPresenter(
  @Assisted private val navigator: Navigator,
  private val observeBookmarks: ObserveBookmarks,
  private val observeSearchResults: ObserveSearchResults,
  private val observeSearchHistory: ObserveSearchHistory,
  private val saveSearchState: SaveSearchState,
  private val clearSearchHistory: ClearSearchHistoryInteractor,
  private val deleteBookmark: DeleteBookmark,
  private val archiveBookmark: ArchiveBookmark,
  private val unarchiveBookmark: UnarchiveBookmark,
) : Presenter<BookmarksUiState> {

  @CircuitInject(BookmarksScreen::class, UserScope::class)
  @AssistedFactory
  interface Factory {
    fun create(navigator: Navigator): BookmarksPresenter
  }

  @Composable
  override fun present(): BookmarksUiState {
    // https://chrisbanes.me/posts/retaining-beyond-viewmodels/#retained-coroutine-scopes
    val presenterScope = rememberRetainedCoroutineScope()

    var searchQuery by rememberRetained { mutableStateOf("") }
    var bookmarkCategory by rememberRetained { mutableStateOf(BookmarkCategory.All) }
    val selectedTags = rememberRetained { mutableStateListOf<Tag>() }

    val bookmarksFlow by produceRetainedState(emptyFlow()) {
      observeBookmarks(
        ObserveBookmarks.Param(
          cached = true,
          query = "",
          category = BookmarkCategory.All,
          tags = selectedTags,
          pagingConfig = PagingConfig(initialLoadSize = 20, pageSize = 20),
        ),
      )
      value = observeBookmarks.flow.cachedIn(presenterScope)
    }
    val bookmarks = bookmarksFlow.collectAsLazyPagingItems()

    val searchResultsFlow by produceRetainedState(
      emptyFlow(),
      searchQuery,
      bookmarkCategory,
      selectedTags.size,
    ) {
      observeSearchResults(
        ObserveSearchResults.Param(
          query = searchQuery,
          category = bookmarkCategory,
          tags = selectedTags,
          pagingConfig = PagingConfig(initialLoadSize = 20, pageSize = 20),
        ),
      )
      value = observeSearchResults.flow.cachedIn(presenterScope)
    }

    val searchResults = searchResultsFlow.collectAsLazyPagingItems()
    var vacatedSearchItems = rememberRetained { mutableStateListOf<Long>() }

    val searchHistoryFlow by produceRetainedState(emptyFlow()) {
      observeSearchHistory(ObserveSearchHistory.Params)
      value = observeSearchHistory.flow
    }
    val searchHistory by searchHistoryFlow.collectAsRetainedState(initial = emptyList())

    return BookmarksUiState(
      bookmarkList = BookmarkListUiState(bookmarks = bookmarks),
      search = SearchUiState(
        query = searchQuery,
        results = searchResults,
        vacatedSearchItems = vacatedSearchItems.toImmutableList(),
        history = searchHistory.toImmutableList(),
        filters = BookmarkFiltersUiState(
          bookmarkCategory = bookmarkCategory,
          selectedTags = selectedTags.toImmutableList(),
        ),
      ),
      eventSink = { event ->
        when (event) {
          is Refresh -> presenterScope.launch { bookmarks.refresh() }

          is ToggleArchive -> presenterScope.launch {
            if (event.bookmark.archived) {
              unarchiveBookmark(event.bookmark.id)
            } else {
              archiveBookmark(event.bookmark.id)
            }
            if (event.source == BookmarkActionSource.Search) {
              vacatedSearchItems.add(event.bookmark.id)
            }
          }

          is Delete -> presenterScope.launch {
            deleteBookmark(event.bookmark.id)
            if (event.source == BookmarkActionSource.Search) {
              vacatedSearchItems.add(event.bookmark.id)
            }
          }

          is Open -> navigator.goTo(UrlScreen(event.bookmark.url))

          is BookmarksUiEvent.Edit -> {
            // TODO: Allow editing bookmarks
          }

          is SelectBookmarkCategory -> {
            bookmarkCategory = event.bookmarkCategory
          }

          AddBookmark -> navigator.goTo(AddBookmarkScreen())
          ShowSettings -> navigator.goTo(SettingsScreen)

          is RemoveTag -> selectedTags.remove(event.tag)
          is SelectTag -> {
            if (!selectedTags.contains(event.tag)) {
              selectedTags.add(0, event.tag)
            }
          }

          is Search -> {
            searchQuery = event.query
            if (event.query.isNotBlank()) {
              presenterScope.launch {
                saveSearchState(
                  param = SaveSearchState.Params(
                    SearchHistory(
                      query = event.query,
                      bookmarkCategory = bookmarkCategory,
                      selectedTags = selectedTags,
                      timestamp = Clock.System.now(),
                    ),
                  ),
                )
              }
            }
            vacatedSearchItems.clear()
          }

          is SelectSearchHistoryItem -> {
            searchQuery = event.searchHistory.query
            bookmarkCategory = event.searchHistory.bookmarkCategory
            selectedTags.clear()
            selectedTags.addAll(event.searchHistory.selectedTags)
            vacatedSearchItems.clear()
          }

          ClearSearch -> {
            searchQuery = ""
            bookmarkCategory = BookmarkCategory.All
            selectedTags.clear()
            vacatedSearchItems.clear()
          }

          ClearSearchHistory -> {
            presenterScope.launch { clearSearchHistory(ClearSearchHistoryInteractor.Params) }
          }
        }
      },
    )
  }
}
