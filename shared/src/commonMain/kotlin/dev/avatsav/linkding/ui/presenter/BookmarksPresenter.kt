package dev.avatsav.linkding.ui.presenter

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import dev.avatsav.linkding.Presenter
import dev.avatsav.linkding.bookmark.application.ports.`in`.BookmarkService
import dev.avatsav.linkding.bookmark.domain.BookmarkError
import io.ktor.http.Url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class BookmarkViewItem(
    val id: Long,
    val title: String,
    val description: String,
    val urlHostName: String,
    val url: String,
    val tagNames: Set<String> = emptySet()
)

class BookmarksPresenter(private val bookmarkService: BookmarkService) : Presenter() {

    data class ViewState(val loading: Boolean, val bookmarks: List<BookmarkViewItem>)

    private val _state = MutableStateFlow(ViewState(true, emptyList()))

    @NativeCoroutinesState
    val uiState: StateFlow<ViewState> = _state

    init {
        presenterScope.launch(Dispatchers.Default) {
            bookmarkService.get().map { bookmarks ->
                val bookmarkItems = bookmarks.results.map { bookmark ->
                    BookmarkViewItem(
                        id = bookmark.id,
                        title = bookmark.getTitleForUi(),
                        description = bookmark.getDescriptionForUi(),
                        url = bookmark.url,
                        urlHostName = Url(bookmark.url).host,
                        tagNames = bookmark.tagNames
                    )
                }
                _state.emit(_state.value.copy(loading = false, bookmarks = bookmarkItems))
            }.mapLeft { error ->
                when (error) {
                    is BookmarkError.CouldNotGetBookmark -> {}
                    BookmarkError.ConfigurationNotSetup -> {}
                }
            }
        }
    }
}