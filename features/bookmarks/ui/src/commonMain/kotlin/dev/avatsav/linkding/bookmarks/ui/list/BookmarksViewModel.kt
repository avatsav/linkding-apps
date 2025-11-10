package dev.avatsav.linkding.bookmarks.ui.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewModelScope
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.AddBookmark
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.ShowSettings
import dev.avatsav.linkding.bookmarks.ui.list.feed.BookmarkFeedPresenter
import dev.avatsav.linkding.bookmarks.ui.list.search.BookmarkSearchPresenter
import dev.avatsav.linkding.viewmodel.MoleculeViewModel
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow

@Inject
class BookmarksViewModel(
  bookmarkFeedPresenterFactory: BookmarkFeedPresenter.Factory,
  bookmarkSearchPresenterFactory: BookmarkSearchPresenter.Factory,
  private val navigator: BookmarksNavigator,
) : MoleculeViewModel<BookmarksUiEvent, BookmarksUiState>() {

  private val feedPresenter = bookmarkFeedPresenterFactory.create(viewModelScope)
  private val searchPresenter = bookmarkSearchPresenterFactory.create(viewModelScope)

  @Composable
  override fun models(events: Flow<BookmarksUiEvent>): BookmarksUiState {

    val feedState by feedPresenter.models.collectAsState()
    val searchState by searchPresenter.models.collectAsState()

    ObserveEvents { event ->
      when (event) {
        AddBookmark -> navigator.navigateToAddBookmark()
        ShowSettings -> navigator.navigateToSettings()
        is BookmarkSearchUiEvent -> searchPresenter.eventSink(event)
        is BookmarkFeedUiEvent -> feedPresenter.eventSink(event)
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
