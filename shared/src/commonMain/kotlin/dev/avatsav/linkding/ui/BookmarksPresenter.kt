package dev.avatsav.linkding.ui

import com.rickclephas.kmp.nativecoroutines.NativeCoroutinesState
import dev.avatsav.linkding.Presenter
import dev.avatsav.linkding.bookmark.application.ports.`in`.BookmarkService
import dev.avatsav.linkding.bookmark.domain.BookmarkError
import dev.avatsav.linkding.ui.model.BookmarkViewItem
import io.ktor.http.Url
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookmarksPresenter(private val bookmarkService: BookmarkService) : Presenter() {

    data class ViewState(val loading: Boolean, val bookmarks: List<BookmarkViewItem>)

    private val _state = MutableStateFlow(ViewState(true, emptyList()))

    @NativeCoroutinesState
    val state: StateFlow<ViewState> = _state

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