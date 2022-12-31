package dev.avatsav.linkding.ui

import dev.avatsav.linkding.Presenter
import dev.avatsav.linkding.bookmark.application.ports.`in`.BookmarkService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class AddBookmarkPresenter(
    private val bookmarkService: BookmarkService
) : Presenter() {

    data class ViewState(val loading: Boolean)

    private val _state = MutableStateFlow(ViewState(true))
    val state: StateFlow<ViewState> = _state


}