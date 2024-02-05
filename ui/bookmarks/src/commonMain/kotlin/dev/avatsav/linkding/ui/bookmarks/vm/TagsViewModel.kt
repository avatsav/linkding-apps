// package dev.avatsav.linkding.ui.bookmarks.vm
//
// import dev.avatsav.linkding.data.tags.TagsRepository
// import dev.avatsav.linkding.paging.Page
// import dev.avatsav.linkding.paging.Pager
// import dev.avatsav.linkding.paging.PagerConfig
// import dev.avatsav.linkding.paging.PagingError
// import dev.avatsav.linkding.ui.AsyncState
// import dev.avatsav.linkding.ui.Uninitialized
// import dev.avatsav.linkding.ui.ViewModel
// import dev.avatsav.linkding.ui.bookmarks.vm.TagsViewState.Companion.Initial
// import io.ktor.http.Url
// import kotlinx.coroutines.flow.MutableStateFlow
// import kotlinx.coroutines.flow.SharingStarted
// import kotlinx.coroutines.flow.StateFlow
// import kotlinx.coroutines.flow.combine
// import kotlinx.coroutines.flow.stateIn
//
// data class TagsViewState(
//    val searchQuery: String = "",
//    val tagsState: AsyncState<List<Tag>, PagingError>,
// ) {
//    companion object {
//        val Initial = TagsViewState("", Uninitialized)
//    }
// }
//
// class TagsViewModel(private val tagsRepository: TagsRepository) : ViewModel() {
//
//    private var tagsPager = Pager(
//        coroutineScope = viewModelScope,
//        pagerConfig = PagerConfig(limit = 30),
//    ) { offset, limit ->
//        val searchQuery = ""
//        tagsRepository.get(offset, limit, searchQuery).fold(
//            ifLeft = { error ->
//                val message = when (error) {
//                    TagError.ConfigurationNotSetup -> "Config not setup"
//                    is TagError.CouldNotGetTag -> error.message
//                }
//                Page.Error(PagingError(message))
//            },
//            ifRight = { tagsList ->
//                Page.Data(tagsList.results, getNextOffset(tagsList.next))
//            },
//        )
//    }
//
//    private val searchQueryState = MutableStateFlow("")
//
//    private val tagsState = tagsPager.stateFlow
//
//    val state: StateFlow<TagsViewState> =
//        combine(tagsState, searchQueryState) { bookmarksState, searchState ->
//            TagsViewState(searchState, bookmarksState)
//        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), Initial)
//
//    init {
//        load()
//    }
//
//    fun load() {
//        tagsPager.loadFirst()
//    }
//
//    fun loadMore() {
//        tagsPager.loadMore()
//    }
//
//    private fun getNextOffset(nextUrl: String?): Int? {
//        if (nextUrl == null) return null
//        return try {
//            val url = Url(nextUrl)
//            return url.parameters["offset"]?.toInt()
//        } catch (_: Throwable) {
//            null
//        }
//    }
// }
