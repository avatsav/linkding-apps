package dev.avatsav.linkding.ui.bookmarks

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import dev.avatsav.linkding.data.bookmarks.BookmarksRepository
import dev.avatsav.linkding.domain.Bookmark
import dev.avatsav.linkding.domain.BookmarkError
import dev.avatsav.linkding.domain.BookmarkFilter
import dev.avatsav.linkding.paging.Page
import dev.avatsav.linkding.paging.Pager
import dev.avatsav.linkding.paging.PagerConfig
import dev.avatsav.linkding.paging.PagingError
import dev.avatsav.linkding.ui.AsyncState
import dev.avatsav.linkding.ui.Uninitialized
import dev.avatsav.linkding.ui.ViewModel
import io.ktor.http.Url
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BookmarkViewItem(
    val id: Long,
    val title: String,
    val description: String,
    val urlHostName: String,
    val url: String,
    val archived: Boolean,
    val tagNames: Set<String> = emptySet(),
) {
    companion object {
        fun fromBookmark(bookmark: Bookmark) = BookmarkViewItem(
            id = bookmark.id,
            title = bookmark.getTitleForUi(),
            description = bookmark.getDescriptionForUi(),
            url = bookmark.url,
            urlHostName = Url(bookmark.url).host,
            archived = bookmark.isArchived,
            tagNames = bookmark.tagNames,
        )
    }
}

data class SearchState(
    val query: String = "",
    val archivedFilterSelected: Boolean = false,
)

data class BookmarksViewState(
    val searchState: SearchState,
    val bookmarksState: AsyncState<List<BookmarkViewItem>, PagingError>,
) {
    companion object {
        val Initial = BookmarksViewState(SearchState(), Uninitialized)
    }
}

class BookmarksViewModel(private val bookmarksRepository: BookmarksRepository) : ViewModel() {

    private val searchState = MutableStateFlow(SearchState())

    private var bookmarksPager = Pager(
        coroutineScope = viewModelScope,
        pagerConfig = PagerConfig(limit = 10),
    ) { offset, limit ->
        val searchState = searchState.value
        val bookmarkFilter =
            if (searchState.archivedFilterSelected) BookmarkFilter.Archived else BookmarkFilter.None

        bookmarksRepository.get(offset, limit, bookmarkFilter, searchState.query).fold(
            ifLeft = { error ->
                val message = when (error) {
                    is BookmarkError.CouldNotGetBookmark -> error.message
                    BookmarkError.ConfigurationNotSetup -> "Config not setup"
                }
                Page.Error(PagingError(message))
            },
            ifRight = { bookmarksList ->
                val bookmarkItems = bookmarksList.results.map { BookmarkViewItem.fromBookmark(it) }
                val nextOffset = getNextOffset(bookmarksList.next)
                Page.Data(bookmarkItems, nextOffset)
            },
        )
    }

    @NativeCoroutinesState
    val state: StateFlow<BookmarksViewState> =
        combine(bookmarksPager.stateFlow, searchState) { bookmarksState, searchState ->
            BookmarksViewState(searchState, bookmarksState)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BookmarksViewState.Initial)

    init {
        load()
    }

    fun load() {
        bookmarksPager.loadFirst()
    }

    fun loadMore() {
        bookmarksPager.loadMore()
    }

    fun setArchivedFilter(selected: Boolean) {
        viewModelScope.launch {
            searchState.emit(searchState.value.copy(archivedFilterSelected = selected))
            load()
        }
    }

    fun toggleArchive(bookmarkId: Long) {

    }

    fun toggleUnread(bookmarkId: Long) {

    }

    private inline fun getNextOffset(nextUrl: String?): Int? {
        if (nextUrl == null) return null
        return try {
            val url = Url(nextUrl)
            return url.parameters["offset"]?.toInt()
        } catch (_: Throwable) {
            null
        }
    }
}
