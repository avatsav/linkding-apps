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
import io.github.aakira.napier.Napier
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
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

    private val urlFlow = MutableStateFlow("")

    private val unfurlStateFlow: Flow<Async<UnfurlData>> =
        urlFlow.debounce(1000).distinctUntilChanged().transform { link ->
            emit(Loading())
            emit(linkUnfurler.unfurl(link).toAsyncState())
        }.flowOn(Dispatchers.Default)

    private val saveStateFlow = MutableStateFlow<Async<Unit>>(Uninitialized)

    val state: StateFlow<AddBookmarkViewState> =
        combine(unfurlStateFlow, saveStateFlow) { unfurlState, saveState ->
            val state = AddBookmarkViewState(unfurlState, saveState)
            Napier.w { "Emitting State: $state" }
            state
        }.stateIn(presenterScope, SharingStarted.WhileSubscribed(), AddBookmarkViewState())

    fun urlChanged(url: String) {
        if (urlFlow.value == url) return
        presenterScope.launch {
            urlFlow.emit(url)
        }
    }

    fun save(url: String, title: String?, description: String?, tags: List<String>) {
        presenterScope.launch {
            saveStateFlow.emit(Loading())
            val saveBookmark = SaveBookmark(url, title, description, tags.toSet())
            bookmarkService.save(saveBookmark).map {
                saveStateFlow.emit(Success(Unit))
            }.mapLeft { error ->
                val failure: Fail<Unit> = when (error) {
                    BookmarkSaveError.ConfigurationNotSetup -> Fail("Linkding not configured")
                    is BookmarkSaveError.CouldNotSaveBookmark -> Fail(error.message.detail)
                }
                saveStateFlow.emit(failure)
            }
        }
    }
}

private fun UnfurlResult.toAsyncState(): Async<UnfurlData> = when (this) {
    is UnfurlResult.Data -> Success(UnfurlData(url, title, description))
    is UnfurlResult.Error -> Fail(message)
}

