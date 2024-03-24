package dev.avatsav.linkding.domain.observers

import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import dev.avatsav.linkding.data.bookmarks.BookmarksRepository
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.domain.PagedObserver
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.tatarka.inject.annotations.Inject

@Inject
class ObserveBookmarksSearch(private val repository: BookmarksRepository) :
    PagedObserver<ObserveBookmarksSearch.Param, Bookmark>() {

    override fun createObservable(params: Param): Flow<PagingData<Bookmark>> {
        if (params.query.isBlank()) return flowOf(PagingData.empty())
        return repository.searchBookmarks(
            pagingConfig = params.pagingConfig,
            query = params.query,
        )
    }

    data class Param(
        val query: String,
        override val pagingConfig: PagingConfig,
    ) : PagedObserver.Param<Bookmark>
}
