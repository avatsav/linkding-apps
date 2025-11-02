package dev.avatsav.linkding.bookmarks.ui.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.AddBookmark
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.ShowSettings
import dev.avatsav.linkding.bookmarks.ui.list.feed.BookmarkFeedPresenter
import dev.avatsav.linkding.bookmarks.ui.list.search.BookmarkSearchPresenter
import dev.avatsav.linkding.inject.UserScope
import dev.avatsav.linkding.ui.AddBookmarkScreen
import dev.avatsav.linkding.ui.BookmarksScreen
import dev.avatsav.linkding.ui.SettingsScreen
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject

@AssistedInject
class BookmarksPresenter(
  @Assisted private val navigator: Navigator,
  private val feedPresenterFactory: BookmarkFeedPresenter.Factory,
  private val searchPresenterFactory: BookmarkSearchPresenter.Factory,
) : Presenter<BookmarksUiState> {

  @CircuitInject(BookmarksScreen::class, UserScope::class)
  @AssistedFactory
  interface Factory {
    fun create(navigator: Navigator): BookmarksPresenter
  }

  @Composable
  override fun present(): BookmarksUiState {
    val feedPresenter = remember { feedPresenterFactory.create(navigator) }
    val searchPresenter = remember { searchPresenterFactory.create(navigator) }

    val feedState = feedPresenter.present()
    val searchState = searchPresenter.present()

    return BookmarksUiState(
      feedState = feedState,
      searchState = searchState,
      eventSink = { event ->
        when (event) {
          AddBookmark -> navigator.goTo(AddBookmarkScreen())
          ShowSettings -> navigator.goTo(SettingsScreen)
        }
      },
    )
  }
}
