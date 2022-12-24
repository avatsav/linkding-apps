package dev.avatsav.linkding.ui

import dev.avatsav.linkding.Presenter
import dev.avatsav.linkding.bookmark.application.ports.`in`.BookmarkService
import dev.avatsav.linkding.bookmark.domain.Bookmark
import dev.avatsav.linkding.bookmark.domain.BookmarkError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class BookmarksPresenter(private val bookmarkService: BookmarkService) : Presenter() {

    data class ViewState(val loading: Boolean, val bookmarks: List<Bookmark>)

    private val _state = MutableStateFlow(ViewState(true, emptyList()))
    val state: StateFlow<ViewState> = _state

    init {
        presenterScope.launch {
            bookmarkService.get().map { bookmarks ->
                _state.emit(_state.value.copy(loading = false, bookmarks = bookmarks.results))
            }.mapLeft { error ->
                when (error) {
                    is BookmarkError.CouldNotGetBookmark -> {}
                    BookmarkError.CredentialsNotSetup -> {}
                }
            }
        }
    }

}