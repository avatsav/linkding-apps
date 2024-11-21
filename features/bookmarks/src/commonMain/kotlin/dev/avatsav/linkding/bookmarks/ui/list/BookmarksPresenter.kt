package dev.avatsav.linkding.bookmarks.ui.list

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.paging.PagingConfig
import androidx.paging.compose.collectAsLazyPagingItems
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.retained.rememberRetained
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.AddBookmark
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.ClearSearch
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.Delete
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.Open
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.RemoveTag
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.Search
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.SelectTag
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.SetBookmarkCategory
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.ShowSettings
import dev.avatsav.linkding.bookmarks.ui.list.BookmarksUiEvent.ToggleArchive
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.domain.interactors.ArchiveBookmark
import dev.avatsav.linkding.domain.interactors.DeleteBookmark
import dev.avatsav.linkding.domain.interactors.UnarchiveBookmark
import dev.avatsav.linkding.domain.observers.ObserveBookmarks
import dev.avatsav.linkding.domain.observers.ObserveSearchResults
import dev.avatsav.linkding.inject.UserScope
import dev.avatsav.linkding.internet.ConnectivityObserver
import dev.avatsav.linkding.ui.AddBookmarkScreen
import dev.avatsav.linkding.ui.BookmarksScreen
import dev.avatsav.linkding.ui.SettingsScreen
import dev.avatsav.linkding.ui.UrlScreen
import dev.avatsav.linkding.ui.circuit.RetainedLaunchedEffect
import dev.avatsav.linkding.ui.circuit.rememberRetainedCachedPagingFlow
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import kotlinx.coroutines.launch

@CircuitInject(BookmarksScreen::class, UserScope::class)
class BookmarksPresenter @Inject constructor(
    @Assisted private val navigator: Navigator,
    private val observeBookmarks: ObserveBookmarks,
    private val observeSearchResults: ObserveSearchResults,
    private val deleteBookmark: DeleteBookmark,
    private val archiveBookmark: ArchiveBookmark,
    private val unarchiveBookmark: UnarchiveBookmark,
    private val connectivityObserver: ConnectivityObserver,
) : Presenter<BookmarksUiState> {

    @Composable
    override fun present(): BookmarksUiState {
        val coroutineScope = rememberCoroutineScope()

        // Shame we have to do this to retain the same flow on navigating back.
        val retainedObserveBookmarks = rememberRetained { observeBookmarks }
        val bookmarks = retainedObserveBookmarks.flow
            .rememberRetainedCachedPagingFlow()
            .collectAsLazyPagingItems()

        val retainedObserveSearchResults = rememberRetained { observeSearchResults }
        val searchResults = retainedObserveSearchResults.flow
            .rememberRetainedCachedPagingFlow()
            .collectAsLazyPagingItems()

        val isOnline by connectivityObserver.observeIsOnline
            .collectAsRetainedState()

        var searchQuery by rememberRetained { mutableStateOf("") }
        var category by rememberRetained { mutableStateOf(BookmarkCategory.All) }
        val selectedTags = rememberRetained { mutableStateListOf<Tag>() }

        RetainedLaunchedEffect(category, selectedTags.size) {
            retainedObserveBookmarks(
                ObserveBookmarks.Param(
                    cached = true,
                    query = "",
                    category = category,
                    tags = selectedTags,
                    pagingConfig = PagingConfig(
                        initialLoadSize = 20,
                        pageSize = 20,
                    ),
                ),
            )
        }

        RetainedLaunchedEffect(searchQuery) {
            retainedObserveSearchResults(
                ObserveSearchResults.Param(
                    query = searchQuery,
                    category = BookmarkCategory.All,
                    tags = emptyList(),
                    pagingConfig = PagingConfig(
                        initialLoadSize = 20,
                        pageSize = 20,
                    ),
                ),
            )
        }

        return BookmarksUiState(
            bookmarkCategory = category,
            bookmarks = bookmarks,
            searchResults = searchResults,
            selectedTags = selectedTags,
            isOnline = isOnline,
        ) { event ->
            when (event) {
                is BookmarksUiEvent.Refresh -> coroutineScope.launch {
                    bookmarks.refresh()
                }

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
                    category = event.bookmarkCategory
                }

                AddBookmark -> navigator.goTo(AddBookmarkScreen())
                ShowSettings -> navigator.goTo(SettingsScreen)
                is RemoveTag -> selectedTags.remove(event.tag)
                is SelectTag -> {
                    if (!selectedTags.contains(event.tag)) {
                        selectedTags.add(0, event.tag)
                    }
                }

                is Search -> searchQuery = event.query
                ClearSearch -> {
                    searchQuery = ""
                }
            }
        }
    }
}
