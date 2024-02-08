package dev.avatsav.linkding.ui.bookmarks

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.paging.PagingConfig
import app.cash.paging.compose.collectAsLazyPagingItems
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.domain.interactors.ArchiveBookmark
import dev.avatsav.linkding.domain.interactors.DeleteBookmark
import dev.avatsav.linkding.domain.interactors.PagedBookmarks
import dev.avatsav.linkding.ui.BookmarksScreen
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.Archive
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.Delete
import dev.avatsav.linkding.ui.bookmarks.BookmarksUiEvent.Refresh
import dev.avatsav.linkding.ui.extensions.rememberCachedPagingFlow
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
    private val pagedBookmarks: PagedBookmarks,
    private val deleteBookmark: DeleteBookmark,
    private val archiveBookmark: ArchiveBookmark,
    private val logger: Logger,
) : Presenter<BookmarksUiState> {

    @Composable
    override fun present(): BookmarksUiState {
        val coroutineScope = rememberCoroutineScope()
        val bookmarks = pagedBookmarks.flow
            .rememberCachedPagingFlow(coroutineScope)
            .collectAsLazyPagingItems()

        LaunchedEffect(Unit) {
            pagedBookmarks.invoke(PagedBookmarks.Parameters(PAGING_CONFIG))
        }

        fun eventSink(event: BookmarksUiEvent) = when (event) {
            Refresh -> bookmarks.refresh()
            is Archive -> {}
            is Delete -> {}
            BookmarksUiEvent.AddBookmark -> {}
            is BookmarksUiEvent.Open -> {}
        }

        return BookmarksUiState(bookmarks, ::eventSink)
    }

    companion object {
        val PAGING_CONFIG = PagingConfig(
            pageSize = 20,
            initialLoadSize = 60,
        )
    }
}
