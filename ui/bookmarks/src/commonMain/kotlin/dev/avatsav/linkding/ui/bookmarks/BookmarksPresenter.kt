package dev.avatsav.linkding.ui.bookmarks

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import app.cash.paging.LoadStateError
import app.cash.paging.LoadStateLoading
import app.cash.paging.LoadStateNotLoading
import app.cash.paging.PagingConfig
import app.cash.paging.compose.collectAsLazyPagingItems
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.internal.rememberStableCoroutineScope
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.domain.interactors.ArchiveBookmark
import dev.avatsav.linkding.domain.interactors.DeleteBookmark
import dev.avatsav.linkding.domain.interactors.UnarchiveBookmark
import dev.avatsav.linkding.domain.observers.ObserveBookmarks
import dev.avatsav.linkding.internet.ConnectivityObserver
import dev.avatsav.linkding.ui.AddBookmarkScreen
import dev.avatsav.linkding.ui.BookmarksScreen
import dev.avatsav.linkding.ui.SettingsScreen
import dev.avatsav.linkding.ui.UrlScreen
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.AddBookmark
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.Delete
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.Open
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.RemoveTag
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.SelectTag
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.SetBookmarkCategory
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.ShowSettings
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.ToggleArchive
import dev.avatsav.linkding.ui.compose.rememberCachedPagingFlow
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
    private val unarchiveBookmark: UnarchiveBookmark,
    private val connectivityObserver: ConnectivityObserver,
) : Presenter<BookmarksUiState> {

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun present(): BookmarksUiState {
        val coroutineScope = rememberStableCoroutineScope()

        val bookmarks = observeBookmarks.flow.rememberCachedPagingFlow(coroutineScope)
            .collectAsLazyPagingItems()

        val isOnline by connectivityObserver.observeIsOnline
            .collectAsState()

        var bookmarkCategory by rememberRetained { mutableStateOf(BookmarkCategory.All) }

        val selectedTags = rememberRetained { mutableStateListOf<Tag>() }

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

        LaunchedEffect(bookmarkCategory) {
            observeBookmarks(
                ObserveBookmarks.Param(
                    bookmarkCategory,
                    PagingConfig(
                        initialLoadSize = 20,
                        pageSize = 20,
                    ),
                ),
            )
        }

        return BookmarksUiState(
            bookmarkCategory = bookmarkCategory,
            bookmarks = bookmarks,
            selectedTags = selectedTags,
            isOnline = isOnline,
            pullToRefreshState = pullToRefreshState,
        ) { event ->
            when (event) {
                is ToggleArchive -> coroutineScope.launch {
                    if (event.bookmark.archived) {
                        unarchiveBookmark(event.bookmark.id)
                    } else {
                        archiveBookmark(event.bookmark.id)
                    }
                }

                is Delete -> coroutineScope.launch {
                    deleteBookmark(event.bookmark.id)
                }

                is Open -> navigator.goTo(UrlScreen(event.bookmark.url))
                is SetBookmarkCategory -> {
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
            }
        }
    }
}
