package dev.avatsav.linkding.domain.observers

import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import dev.avatsav.linkding.data.bookmarks.BookmarksRepository
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.domain.PagedObserver
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class ObserveBookmarks(private val repository: BookmarksRepository) :
    PagedObserver<ObserveBookmarks.Param, Bookmark>() {

    override fun createObservable(params: Param): Flow<PagingData<Bookmark>> {
        return repository.getBookmarksPaged(
            pagingConfig = params.pagingConfig,
            category = params.bookmarkCategory,
        )
    }

    data class Param(
        val bookmarkCategory: BookmarkCategory = BookmarkCategory.All,
        override val pagingConfig: PagingConfig,
    ) : PagedObserver.Param<Bookmark>
}
