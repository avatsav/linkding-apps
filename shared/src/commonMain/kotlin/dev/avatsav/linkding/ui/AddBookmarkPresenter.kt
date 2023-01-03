package dev.avatsav.linkding.ui

import dev.avatsav.linkding.LinkUnfurler
import dev.avatsav.linkding.Presenter
import dev.avatsav.linkding.UnfurlResult
import dev.avatsav.linkding.bookmark.application.ports.`in`.BookmarkService
import dev.avatsav.linkding.bookmark.domain.SaveBookmark
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(FlowPreview::class)
class AddBookmarkPresenter(
    private val bookmarkService: BookmarkService,
    private val linkUnfurler: LinkUnfurler,
) : Presenter() {

    data class ViewState(
        val unfluredTitle: String? = null, val unfluredDescription: String? = null
    )

    private val _state = MutableStateFlow(ViewState())
    val state: StateFlow<ViewState> = _state

    private val linkFlow = MutableStateFlow("")

    init {
        presenterScope.launch {
            withContext(Dispatchers.Default) {
                linkFlow.debounce(2000).distinctUntilChanged()
                    .map { link -> linkUnfurler.unfurl(link) }.flowOn(Dispatchers.Default)
                    .collectLatest {
                        when (it) {
                            is UnfurlResult.Data -> {
                                _state.emit(
                                    _state.value.copy(
                                        unfluredTitle = it.title,
                                        unfluredDescription = it.description
                                    )
                                )
                                Napier.w { "emitting state with $it unfurled" }
                            }

                            is UnfurlResult.Error -> {}
                        }
                    }
            }
        }
    }

    fun setLink(link: String) {
        presenterScope.launch {
            linkFlow.emit(link)
        }
    }

    fun save(saveBookmark: SaveBookmark) {/* TODO */
        presenterScope.launch {
            _state.emit(_state.value.copy())
            bookmarkService.save(saveBookmark).map {
                // TODO: Post success to the UI and close.
            }.mapLeft {
                // TODO: Post failure to the UI with the message.
            }
        }
    }

}