package dev.avatsav.linkding.bookmarks.ui.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.AddBookmark
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.ShowSettings
import dev.avatsav.linkding.bookmarks.ui.list.feed.BookmarkFeedPresenter
import dev.avatsav.linkding.bookmarks.ui.list.search.BookmarkSearchPresenter
import dev.avatsav.linkding.di.scope.UserScope
import dev.avatsav.linkding.viewmodel.MoleculePresenter
import dev.avatsav.linkding.viewmodel.MoleculeViewModel
import dev.avatsav.linkding.viewmodel.ObserveEffects
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.ContributesIntoMap
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.binding
import dev.zacsweers.metrox.viewmodel.ViewModelKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow

@ContributesIntoMap(UserScope::class, binding<ViewModel>())
@ViewModelKey(BookmarksViewModel::class)
@Inject
class BookmarksViewModel(bookmarksPresenterFactory: BookmarksPresenter.Factory) :
  MoleculeViewModel<BookmarksUiEvent, BookmarksUiState, BookmarkUiEffect>() {
  override val presenter by lazy { bookmarksPresenterFactory.create(viewModelScope) }
}

@AssistedInject
class BookmarksPresenter(
  @Assisted coroutineScope: CoroutineScope,
  bookmarkFeedPresenterFactory: BookmarkFeedPresenter.Factory,
  bookmarkSearchPresenterFactory: BookmarkSearchPresenter.Factory,
) : MoleculePresenter<BookmarksUiEvent, BookmarksUiState, BookmarkUiEffect>(coroutineScope) {

  private val feedPresenter by lazy { bookmarkFeedPresenterFactory.create(coroutineScope) }
  private val searchPresenter by lazy { bookmarkSearchPresenterFactory.create(coroutineScope) }

  @Composable
  override fun models(events: Flow<BookmarksUiEvent>): BookmarksUiState {
    val feedState by feedPresenter.models.collectAsState()
    val searchState by searchPresenter.models.collectAsState()
    ObserveEffects(feedPresenter.effects) { emitEffect(it) }
    ObserveEffects(searchPresenter.effects) { emitEffect(it) }

    ObserveEvents { event ->
      when (event) {
        AddBookmark -> emitEffect(BookmarkUiEffect.AddBookmark)
        ShowSettings -> emitEffect(BookmarkUiEffect.NavigateToSettings)
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
