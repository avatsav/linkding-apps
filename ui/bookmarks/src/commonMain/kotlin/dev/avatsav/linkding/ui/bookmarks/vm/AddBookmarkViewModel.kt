// package dev.avatsav.linkding.ui.bookmarks.vm
//
// import dev.avatsav.linkding.data.bookmarks.BookmarksRepository
// import dev.avatsav.linkding.data.unfurl.LinkUnfurler
// import dev.avatsav.linkding.data.unfurl.UnfurlResult
// import dev.avatsav.linkding.ui.AsyncState
// import dev.avatsav.linkding.ui.Content
// import dev.avatsav.linkding.ui.Fail
// import dev.avatsav.linkding.ui.Loading
// import dev.avatsav.linkding.ui.Uninitialized
// import dev.avatsav.linkding.ui.ViewModel
// import io.github.aakira.napier.Napier
// import kotlinx.coroutines.Dispatchers
// import kotlinx.coroutines.FlowPreview
// import kotlinx.coroutines.flow.Flow
// import kotlinx.coroutines.flow.MutableStateFlow
// import kotlinx.coroutines.flow.SharingStarted
// import kotlinx.coroutines.flow.StateFlow
// import kotlinx.coroutines.flow.combine
// import kotlinx.coroutines.flow.debounce
// import kotlinx.coroutines.flow.distinctUntilChanged
// import kotlinx.coroutines.flow.flowOn
// import kotlinx.coroutines.flow.stateIn
// import kotlinx.coroutines.flow.transform
// import kotlinx.coroutines.launch
//
// data class AddBookmarkViewState(
//    val unfurlState: AsyncState<UnfurlData, UnfurlError> = Uninitialized,
//    val saveState: AsyncState<Bookmark, SaveError> = Uninitialized,
// ) {
//    object UnfurlError
//    data class SaveError(val message: String)
// }
//
// data class UnfurlData(
//    val unfurledUrl: String,
//    val unfurledTitle: String? = null,
//    val unfurledDescription: String? = null,
// )
//
// @OptIn(FlowPreview::class)
// class AddBookmarkViewModel(
//    private val bookmarksRepository: BookmarksRepository,
//    private val linkUnfurler: LinkUnfurler,
// ) : ViewModel() {
//
//    private val urlFlow = MutableStateFlow("")
//
//    private val unfurlStateFlow: Flow<AsyncState<UnfurlData, AddBookmarkViewState.UnfurlError>> =
//        urlFlow.debounce(1000).distinctUntilChanged().transform { link ->
//            emit(Loading())
//            emit(linkUnfurler.unfurl(link).toAsyncState())
//        }.flowOn(Dispatchers.Default)
//
//    private val saveStateFlow =
//        MutableStateFlow<AsyncState<Bookmark, AddBookmarkViewState.SaveError>>(Uninitialized)
//
//    val state: StateFlow<AddBookmarkViewState> =
//        combine(unfurlStateFlow, saveStateFlow) { unfurlState, saveState ->
//            val state = AddBookmarkViewState(unfurlState, saveState)
//            Napier.w { "Emitting State: $state" }
//            state
//        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), AddBookmarkViewState())
//
//    fun urlChanged(url: String) {
//        if (urlFlow.value == url) return
//        viewModelScope.launch {
//            urlFlow.emit(url)
//        }
//    }
//
//    fun save(url: String, title: String?, description: String?, tags: List<String>) {
//        viewModelScope.launch {
//            saveStateFlow.emit(Loading())
//            val saveBookmark = SaveBookmark(url, title, description, tags.toSet())
//            bookmarksRepository.save(saveBookmark).map { bookmark ->
//                saveStateFlow.emit(Content(bookmark))
//            }.mapLeft { error ->
//                val failure: Fail<AddBookmarkViewState.SaveError> = when (error) {
//                    BookmarkSaveError.ConfigurationNotSetup -> Fail(AddBookmarkViewState.SaveError("Linkding not configured"))
//                    is BookmarkSaveError.CouldNotSaveBookmark -> Fail(
//                        AddBookmarkViewState.SaveError(
//                            error.message,
//                        ),
//                    )
//                }
//                saveStateFlow.emit(failure)
//            }
//        }
//    }
// }
//
// private fun UnfurlResult.toAsyncState(): AsyncState<UnfurlData, AddBookmarkViewState.UnfurlError> =
//    when (this) {
//        is UnfurlResult.Data -> Content(UnfurlData(url, title, description))
//        is UnfurlResult.Error -> Fail(dev.avatsav.linkding.ui.bookmarks.vm.AddBookmarkViewState.UnfurlError)
//    }
