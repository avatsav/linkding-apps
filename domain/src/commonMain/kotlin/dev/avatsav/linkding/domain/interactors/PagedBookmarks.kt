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
    PagedObserver<PagedBookmarks.Param, Bookmark>() {

    override fun createObservable(params: Param): Flow<PagingData<Bookmark>> =
        Pager(PagingConfig(pageSize = 10)) { sourceFactory.create() }.flow

    data class Param(
        override val pagingConfig: PagingConfig,
    ) : PagedObserver.Param<Bookmark>
}
