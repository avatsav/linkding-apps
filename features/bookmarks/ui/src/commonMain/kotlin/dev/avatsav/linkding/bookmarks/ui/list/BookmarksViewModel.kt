package dev.avatsav.linkding.bookmarks.ui.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewModelScope
import dev.avatsav.linkding.bookmarks.api.interactors.ArchiveBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.ClearSearchHistory
import dev.avatsav.linkding.bookmarks.api.interactors.DeleteBookmark
import dev.avatsav.linkding.bookmarks.api.interactors.SaveSearchState
import dev.avatsav.linkding.bookmarks.api.interactors.UnarchiveBookmark
import dev.avatsav.linkding.bookmarks.api.observers.ObserveBookmarks
import dev.avatsav.linkding.bookmarks.api.observers.ObserveSearchHistory
import dev.avatsav.linkding.bookmarks.api.observers.ObserveSearchResults
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.AddBookmark
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.ShowSettings
import dev.avatsav.linkding.bookmarks.ui.list.feed.BookmarkFeedNavigator
import dev.avatsav.linkding.bookmarks.ui.list.feed.BookmarkFeedPresenter
import dev.avatsav.linkding.bookmarks.ui.list.search.BookmarkSearchNavigator
import dev.avatsav.linkding.bookmarks.ui.list.search.BookmarkSearchPresenter
import dev.avatsav.linkding.viewmodel.MoleculeViewModel
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class BookmarksViewModel(
  private val observeBookmarks: ObserveBookmarks,
  private val observeSearchResults: ObserveSearchResults,
  private val observeSearchHistory: ObserveSearchHistory,
  private val deleteBookmark: DeleteBookmark,
  private val archiveBookmark: ArchiveBookmark,
  private val unarchiveBookmark: UnarchiveBookmark,
  private val saveSearchState: SaveSearchState,
  private val clearSearchHistory: ClearSearchHistory,
  private val navigator: BookmarksNavigator,
) : MoleculeViewModel<BookmarksUiEvent, BookmarksUiState>() {

  // Create child presenters as properties so their eventSinks are accessible
  private val feedPresenter =
    BookmarkFeedPresenter(
      scope = viewModelScope,
      observeBookmarks = observeBookmarks,
      deleteBookmark = deleteBookmark,
      archiveBookmark = archiveBookmark,
      unarchiveBookmark = unarchiveBookmark,
      navigator =
        object : BookmarkFeedNavigator {
          override fun openUrl(url: String) {
            navigator.navigateToUrl(url)
          }
        },
    )

  private val searchPresenter =
    BookmarkSearchPresenter(
      scope = viewModelScope,
      observeSearchResults = observeSearchResults,
      observeSearchHistory = observeSearchHistory,
      saveSearchState = saveSearchState,
      clearSearchHistory = clearSearchHistory,
      deleteBookmark = deleteBookmark,
      archiveBookmark = archiveBookmark,
      unarchiveBookmark = unarchiveBookmark,
      navigator =
        object : BookmarkSearchNavigator {
          override fun openUrl(url: String) {
            navigator.navigateToUrl(url)
          }
        },
    )

  // Expose child eventSinks
  val feedEventSink = feedPresenter::eventSink
  val searchEventSink = searchPresenter::eventSink

  @Composable
  override fun models(events: Flow<BookmarksUiEvent>): BookmarksUiState {

    // Collect states from child presenters
    val feedState by feedPresenter.models.collectAsState()
    val searchState by searchPresenter.models.collectAsState()

    CollectEvents { event ->
      when (event) {
        AddBookmark -> navigator.navigateToAddBookmark()
        ShowSettings -> navigator.navigateToSettings()
      }
    }

    return BookmarksUiState(feedState = feedState, searchState = searchState)
  }
}

interface BookmarksNavigator {
  fun navigateToAddBookmark()

  fun navigateToSettings()

  fun navigateToUrl(url: String)
}
