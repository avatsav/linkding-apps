package dev.avatsav.linkding.paging

import dev.avatsav.linkding.ui.AsyncState
import dev.avatsav.linkding.ui.Fail
import dev.avatsav.linkding.ui.Loading
import dev.avatsav.linkding.ui.PageStatus
import dev.avatsav.linkding.ui.PagedContent
import dev.avatsav.linkding.ui.Uninitialized
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.coroutines.cancellation.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

data class PagingError(val message: String)

internal interface Page<V : Any> {
    data class Data<V : Any>(val value: List<V>, val nextOffset: Int?) : Page<V>
    data class Error(val error: PagingError) : Page<Nothing>
}

data class PagerConfig(val limit: Int, val firstPageLimit: Int = limit)

internal interface Pager<V : Any> {
    val stateFlow: StateFlow<AsyncState<List<V>, PagingError>>
    fun loadFirst()
    fun loadMore()
    fun remove(item: V)
    fun cancel()
}

internal fun <V : Any> Pager(
    coroutineScope: CoroutineScope,
    pagerConfig: PagerConfig,
    load: suspend (Int, Int) -> Page<out V>,
): Pager<V> {
    return DefaultPager(coroutineScope, pagerConfig, load)
}

private class DefaultPager<V : Any>(
    private val coroutineScope: CoroutineScope,
    private val pagerConfig: PagerConfig,
    private val load: suspend (Int, Int) -> Page<out V>,
) : Pager<V> {

    private val state: MutableStateFlow<AsyncState<List<V>, PagingError>> =
        MutableStateFlow(Uninitialized)
    override val stateFlow: StateFlow<AsyncState<List<V>, PagingError>>
        get() = state

    private var loadingJob: Job? = null
    private var nextOffset: Int? = 0

    override fun loadFirst() {
        if (state.value is Loading) return
        loadingJob?.cancel()
        loadingJob = coroutineScope.launch {
            try {
                val page = load(0, pagerConfig.firstPageLimit)
                if (isActive) {
                    when (page) {
                        is Page.Data -> {
                            nextOffset = page.nextOffset
                            val items = page.value
                            val pageStatus = hasMoreOrComplete(page.nextOffset)
                            state.emit(PagedContent(items, pageStatus))
                        }
                        is Page.Error -> {
                            state.emit(Fail(page.error))
                        }
                    }
                }
            } catch (e: CancellationException) {
                throw e
            }
        }
    }

    override fun loadMore() {
        val pageIndex = nextOffset ?: return
        canLoadMore(state.value) { currentPagedContent ->
            loadingJob?.cancel()
            loadingJob = coroutineScope.launch {
                state.emit(currentPagedContent.copy(status = PageStatus.LoadingMore))
                try {
                    val page = load(pageIndex, pagerConfig.firstPageLimit)
                    if (isActive) {
                        when (page) {
                            is Page.Data -> {
                                nextOffset = page.nextOffset
                                val newItems = page.value
                                val currentItems = currentPagedContent.value
                                val pageStatus = hasMoreOrComplete(page.nextOffset)
                                state.emit(PagedContent(currentItems + newItems, pageStatus))
                            }
                            is Page.Error -> {
                                state.emit(currentPagedContent.copy(status = PageStatus.ErrorLoadingMore))
                            }
                        }
                    }
                } catch (e: CancellationException) {
                    throw e
                }
            }
        }
    }

    override fun remove(item: V) {
        coroutineScope.launch(Dispatchers.Default) {
            val currentState = state.value
            if (currentState is PagedContent && currentState.status != PageStatus.LoadingMore) {
                val newList = currentState.value.filterNot { it == item }
                state.emit(PagedContent(newList, currentState.status))
            }
        }
    }

    override fun cancel() {
        loadingJob?.cancel()
    }

    private inline fun hasMoreOrComplete(nextPageIndex: Int?) = if (nextPageIndex == null) {
        PageStatus.Complete
    } else {
        PageStatus.HasMore
    }

    @OptIn(ExperimentalContracts::class)
    private inline fun <V : Any> canLoadMore(
        state: AsyncState<V, PagingError>,
        action: (PagedContent<V>) -> Unit,
    ) {
        contract { callsInPlace(action, InvocationKind.AT_MOST_ONCE) }
        when (state) {
            is PagedContent -> when (state.status) {
                PageStatus.HasMore, PageStatus.ErrorLoadingMore -> action(state)
                else -> return
            }
            else -> return
        }
    }

}
