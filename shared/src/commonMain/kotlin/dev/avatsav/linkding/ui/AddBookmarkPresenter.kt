package dev.avatsav.linkding.ui

import dev.avatsav.linkding.LinkUnfurler
import dev.avatsav.linkding.Presenter
import dev.avatsav.linkding.UnfurlResult
import dev.avatsav.linkding.bookmark.application.ports.`in`.BookmarkService
import dev.avatsav.linkding.bookmark.domain.BookmarkSaveError
import dev.avatsav.linkding.bookmark.domain.SaveBookmark
import dev.avatsav.linkding.ui.model.Async
import dev.avatsav.linkding.ui.model.Fail
import dev.avatsav.linkding.ui.model.Loading
import dev.avatsav.linkding.ui.model.Success
import dev.avatsav.linkding.ui.model.Uninitialized
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

data class AddBookmarkViewState(
    val unfurlState: Async<UnfurlData> = Uninitialized, val saveState: Async<Unit> = Uninitialized
)

data class UnfurlData(
    val unfurledUrl: String,
    val unfurledTitle: String? = null,
    val unfurledDescription: String? = null
)

@OptIn(FlowPreview::class)
class AddBookmarkPresenter(
    private val bookmarkService: BookmarkService,
    private val linkUnfurler: LinkUnfurler,
) : Presenter() {

    private val _state = MutableStateFlow(AddBookmarkViewState())
    val state: StateFlow<AddBookmarkViewState> = _state

    private val linkFlow = MutableStateFlow("")

    init {
        linkFlow.debounce(1000).distinctUntilChanged()
            .onEach { _state.emit(value = _state.value.copy(unfurlState = Loading())) }
            .map { link -> linkUnfurler.unfurl(link) }
            .flowOn(Dispatchers.Default)
            .onEach { unfurlResult ->
                when (unfurlResult) {
                    is UnfurlResult.Data -> {
                        _state.value = _state.value.copy(
                            unfurlState = Success(
                                UnfurlData(
                                    unfurledUrl = unfurlResult.url,
                                    unfurledTitle = unfurlResult.title,
                                    unfurledDescription = unfurlResult.description
                                )
                            ),
                        )
                    }

                    is UnfurlResult.Error -> {
                        _state.value = _state.value.copy(unfurlState = Fail(unfurlResult.message))
                    }
                }
            }.launchIn(presenterScope)
    }

    fun setLink(link: String) {
        if (linkFlow.value == link) return
        presenterScope.launch {
            linkFlow.emit(link)
        }
    }

    fun save(url: String, title: String?, description: String?, tags: List<String>) {
        val saveBookmark = SaveBookmark(url, title, description, tags.toSet())
        presenterScope.launch {
            _state.emit(_state.value.copy(saveState = Loading()))
            bookmarkService.save(saveBookmark).map {
                _state.emit(_state.value.copy(saveState = Success(Unit)))
            }.mapLeft { error ->
                val failure: Fail<Unit> = when (error) {
                    BookmarkSaveError.ConfigurationNotSetup -> Fail("Linkding not configured")
                    is BookmarkSaveError.CouldNotSaveBookmark -> Fail(error.message.detail)
                }
                _state.emit(_state.value.copy(saveState = failure))
            }
        }
    }

}