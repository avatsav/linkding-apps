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

@Inject
class BookmarksPagingDataFactory(
    private val bookmarksRemoteMediatorFactory: BookmarksRemoteMediatorFactory,
    private val pagingBookmarksDao: PagingBookmarksDao,
) {

    @OptIn(ExperimentalPagingApi::class)
    fun create(
        pagingConfig: PagingConfig,
        query: String = "",
        category: BookmarkCategory = BookmarkCategory.All,
    ): Flow<PagingData<Bookmark>> {
        return Pager(
            config = pagingConfig,
            remoteMediator = bookmarksRemoteMediatorFactory(query, category),
            pagingSourceFactory = { pagingBookmarksDao.keyedPagingSource() },
        ).flow
    }
}
