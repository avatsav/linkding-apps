package dev.avatsav.linkding.domain.observers

import androidx.paging.ExperimentalPagingApi
import androidx.paging.PagingConfig
import app.cash.paging.PagingData
import dev.avatsav.linkding.data.bookmarks.BookmarksRepository
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.domain.PagedObserver
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@OptIn(ExperimentalPagingApi::class)
@Inject
class ObserveBookmarks(private val repository: BookmarksRepository) :
    PagedObserver<ObserveBookmarks.Param, Bookmark>() {

    override fun createObservable(params: Param): Flow<PagingData<Bookmark>> {
        return repository.getBookmarksPaged(params.pagingConfig)
    }

    data class Param(override val pagingConfig: PagingConfig) : PagedObserver.Param<Bookmark>
}
