package dev.avatsav.linkding.bookmarks.ui.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewModelScope
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.AddBookmark
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.ShowSettings
import dev.avatsav.linkding.bookmarks.ui.list.feed.BookmarkFeedPresenter
import dev.avatsav.linkding.bookmarks.ui.list.search.BookmarkSearchPresenter
import dev.avatsav.linkding.viewmodel.MoleculePresenter
import dev.avatsav.linkding.viewmodel.MoleculeViewModel
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

@Inject
class BookmarksViewModel(bookmarksPresenterFactory: BookmarksPresenter.Factory) :
  MoleculeViewModel<BookmarksUiEvent, BookmarksUiState, BookmarksEffect>() {
  override val presenter by lazy { bookmarksPresenterFactory.create(viewModelScope) }
}

@AssistedInject
class BookmarksPresenter(
  @Assisted coroutineScope: CoroutineScope,
  bookmarkFeedPresenterFactory: BookmarkFeedPresenter.Factory,
  bookmarkSearchPresenterFactory: BookmarkSearchPresenter.Factory,
) : MoleculePresenter<BookmarksUiEvent, BookmarksUiState, BookmarksEffect>(coroutineScope) {

  private val feedPresenter by lazy { bookmarkFeedPresenterFactory.create(coroutineScope) }
  private val searchPresenter by lazy { bookmarkSearchPresenterFactory.create(coroutineScope) }

  @Composable
  override fun models(events: Flow<BookmarksUiEvent>): BookmarksUiState {

    val feedState by feedPresenter.models.collectAsState()
    val searchState by searchPresenter.models.collectAsState()

    ObserveEvents { event ->
      when (event) {
        AddBookmark -> trySendEffect(BookmarksEffect.NavigateToAddBookmark)
        ShowSettings -> trySendEffect(BookmarksEffect.NavigateToSettings)
        is BookmarkSearchUiEvent -> searchPresenter.eventSink(event)
        is BookmarkFeedUiEvent -> feedPresenter.eventSink(event)
      }
    }

    return BookmarksUiState(feedState = feedState, searchState = searchState)
  }

  @AssistedFactory
  interface Factory {
    fun create(scope: CoroutineScope): BookmarksPresenter
  }
}
