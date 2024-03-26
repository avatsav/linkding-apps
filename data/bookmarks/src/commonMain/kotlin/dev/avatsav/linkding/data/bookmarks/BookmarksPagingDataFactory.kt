package dev.avatsav.linkding.data.bookmarks

import androidx.paging.ExperimentalPagingApi
import app.cash.paging.Pager
import app.cash.paging.PagingConfig
import app.cash.paging.PagingData
import dev.avatsav.linkding.data.db.daos.PagingBookmarksDao
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.BookmarkCategory
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@OptIn(ExperimentalPagingApi::class)
@Inject
class BookmarksPagingDataFactory(
    private val bookmarksRemoteMediatorFactory: BookmarksRemoteMediatorFactory,
    private val remoteBookmarksPagingSourceFactory: RemoteBookmarksPagingSourceFactory,
    private val pagingBookmarksDao: PagingBookmarksDao,
) {
    fun create(
        cached: Boolean,
        pagingConfig: PagingConfig,
        param: Param,
    ): Flow<PagingData<Bookmark>> {
        return if (cached) {
            cachedBookmarksFlow(pagingConfig, param)
        } else {
            onlineOnlyBookmarksFlow(pagingConfig, param)
        }
    }

    private fun cachedBookmarksFlow(pagingConfig: PagingConfig, param: Param) = Pager(
        config = pagingConfig,
        pagingSourceFactory = {
            remoteBookmarksPagingSourceFactory(
                param.query,
                param.category,
                param.tags,
            )
        },
    ).flow

    private fun onlineOnlyBookmarksFlow(pagingConfig: PagingConfig, param: Param) = Pager(
        config = pagingConfig,
        remoteMediator = bookmarksRemoteMediatorFactory(
            param.query,
            param.category,
            param.tags,
        ),
        pagingSourceFactory = { pagingBookmarksDao.offsetPagingSource() },
    ).flow

    data class Param(
        val query: String = "",
        val category: BookmarkCategory = BookmarkCategory.All,
        val tags: List<String> = emptyList(),
    )
}
