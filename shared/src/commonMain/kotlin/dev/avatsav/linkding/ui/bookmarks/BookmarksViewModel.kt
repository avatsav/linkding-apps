package dev.avatsav.linkding.ui.bookmarks

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import dev.avatsav.linkding.ui.ViewModel
import dev.avatsav.linkding.data.bookmarks.BookmarksRepository
import dev.avatsav.linkding.domain.Bookmark
import dev.avatsav.linkding.domain.BookmarkError
import dev.avatsav.linkding.paging.Page
import dev.avatsav.linkding.paging.Pager
import dev.avatsav.linkding.paging.PagerConfig
import dev.avatsav.linkding.paging.PagingError
import dev.avatsav.linkding.ui.AsyncState
import dev.avatsav.linkding.ui.Uninitialized
import io.ktor.http.Url
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

data class BookmarkViewItem(
    val id: Long,
    val title: String,
    val description: String,
    val urlHostName: String,
    val url: String,
    val tagNames: Set<String> = emptySet()
) {
    companion object {
        fun fromBookmark(bookmark: Bookmark) = BookmarkViewItem(
            id = bookmark.id,
            title = bookmark.getTitleForUi(),
            description = bookmark.getDescriptionForUi(),
            url = bookmark.url,
            urlHostName = Url(bookmark.url).host,
            tagNames = bookmark.tagNames
        )
    }
}

data class BookmarksViewState(
    val bookmarksState: AsyncState<List<BookmarkViewItem>, PagingError>
) {
    companion object {
        val Initial = BookmarksViewState(Uninitialized)
    }
}

class BookmarksViewModel(private val bookmarksRepository: BookmarksRepository) : ViewModel() {

    private var bookmarksPager = Pager(
        coroutineScope = viewModelScope, pagerConfig = PagerConfig(limit = 10)
    ) { offset, limit ->
        bookmarksRepository.get(offset, limit).fold(ifLeft = { error ->
            val message = when (error) {
                is BookmarkError.CouldNotGetBookmark -> error.message
                BookmarkError.ConfigurationNotSetup -> "Config not setup"
            }
            Page.Error(PagingError(message))
        }, ifRight = { bookmarksList ->
            val bookmarkItems = bookmarksList.results.map { BookmarkViewItem.fromBookmark(it) }
            val nextOffset = getNextOffset(bookmarksList.next)
            Page.Data(bookmarkItems, nextOffset)
        })
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

    private val pagedState: StateFlow<AsyncState<List<BookmarkViewItem>, PagingError>> =
        bookmarksPager.stateFlow

    @NativeCoroutinesState
    val state: StateFlow<BookmarksViewState> = pagedState.map { BookmarksViewState(it) }.stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), BookmarksViewState.Initial
    )

    init {
        load()
    }

    fun load() {
        bookmarksPager.loadFirst()
    }

    fun loadMore() {
        bookmarksPager.loadMore()
    }
}
