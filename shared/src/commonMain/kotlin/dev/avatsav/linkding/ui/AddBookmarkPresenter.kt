package dev.avatsav.linkding.ui

import dev.avatsav.linkding.LinkUnfurler
import dev.avatsav.linkding.Presenter
import dev.avatsav.linkding.UnfurlResult
import dev.avatsav.linkding.bookmark.application.ports.`in`.BookmarkService
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AddBookmarkPresenter(
    private val bookmarkService: BookmarkService,
    private val linkUnfurler: LinkUnfurler,
) : Presenter() {

    data class ViewState(
        val loading: Boolean,
        val url: String?,
        val unfluredTitle: String?,
        val unfluredDescription: String?
    )

    private val _state = MutableStateFlow(ViewState(true, null, null, null))
    val state: StateFlow<ViewState> = _state

    fun setLink(link: String) {
        presenterScope.launch {
            _state.emit(_state.value.copy(loading = true))
            withContext(Dispatchers.Default) {
                when (val result = linkUnfurler.unfurl(link)) {
                    is UnfurlResult.Data -> _state.emit(
                        _state.value.copy(
                            loading = false,
                            url = result.title,
                            unfluredTitle = result.title,
                            unfluredDescription = result.description
                        )
                    )

                    is UnfurlResult.Error -> {
                        Napier.e(result.message)
                    }
                }
            }
        }
    }

}