package dev.avatsav.linkding.ui.bookmarks

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import app.cash.paging.LoadStateError
import app.cash.paging.LoadStateLoading
import app.cash.paging.LoadStateNotLoading
import app.cash.paging.PagingConfig
import app.cash.paging.compose.collectAsLazyPagingItems
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.avatsav.linkding.domain.interactors.ArchiveBookmark
import dev.avatsav.linkding.domain.interactors.DeleteBookmark
import dev.avatsav.linkding.domain.observers.ObserveBookmarks
import dev.avatsav.linkding.ui.AddBookmarkScreen
import dev.avatsav.linkding.ui.BookmarksScreen
import dev.avatsav.linkding.ui.UrlScreen
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.AddBookmark
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.Archive
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.Delete
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.Open
import dev.avatsav.linkding.ui.extensions.rememberCachedPagingFlow
import dev.avatsav.linkding.ui.extensions.rememberStableCoroutineScope
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class BookmarksUiPresenterFactory(
    private val presenterFactory: (Navigator) -> BookmarksPresenter,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? {
        return when (screen) {
            is BookmarksScreen -> presenterFactory(navigator)
            else -> null
        }
    }
}

@Inject
class BookmarksPresenter(
    @Assisted private val navigator: Navigator,
    private val observeBookmarks: ObserveBookmarks,
    private val deleteBookmark: DeleteBookmark,
    private val archiveBookmark: ArchiveBookmark,
) : Presenter<BookmarksUiState> {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun present(): BookmarksUiState {
        val coroutineScope = rememberStableCoroutineScope()
        val bookmarks = observeBookmarks.flow.rememberCachedPagingFlow(coroutineScope)
            .collectAsLazyPagingItems()

        val pullToRefreshState = rememberPullToRefreshState()

        LaunchedEffect(pullToRefreshState.isRefreshing) {
            bookmarks.refresh()
        }

        LaunchedEffect(bookmarks.loadState) {
            when (bookmarks.loadState.refresh) {
                is LoadStateLoading -> Unit
                is LoadStateError, is LoadStateNotLoading -> {
                    pullToRefreshState.endRefresh()
                }
            }
        }

        LaunchedEffect(Unit) {
            observeBookmarks.invoke(
                ObserveBookmarks.Param(
                    PagingConfig(
                        initialLoadSize = 20,
                        pageSize = 20,
                        enablePlaceholders = false,
                    ),
                ),
            )
        }

        return BookmarksUiState(
            bookmarks = bookmarks,
            pullToRefreshState = pullToRefreshState,
        ) { event ->
            when (event) {
                is Archive -> coroutineScope.launch {
                    archiveBookmark(event.bookmark.id)
                }

                is Delete -> coroutineScope.launch {
                    deleteBookmark(event.bookmark.id)
                }

                AddBookmark -> navigator.goTo(AddBookmarkScreen())
                is Open -> navigator.goTo(UrlScreen(event.bookmark.url))
            }
        }
    }
}
