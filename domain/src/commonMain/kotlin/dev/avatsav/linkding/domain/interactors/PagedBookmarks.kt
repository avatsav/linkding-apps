package dev.avatsav.linkding.domain.interactors

import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import dev.avatsav.linkding.data.bookmarks.BookmarksPagingSourceFactory
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.domain.PagedObserver
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class PagedBookmarks(private val sourceFactory: BookmarksPagingSourceFactory) :
    PagedObserver<PagedBookmarks.Parameters, Bookmark>() {

    override fun createObservable(params: Parameters): Flow<PagingData<Bookmark>> =
        Pager(PagingConfig(pageSize = 10)) { sourceFactory.create() }.flow

    data class Parameters(
        override val pagingConfig: PagingConfig,
    ) : PagedObserver.Parameters<Bookmark>
}
