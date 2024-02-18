package dev.avatsav.linkding.data.db.sqldelight.daos

import app.cash.paging.PagingSource
import app.cash.sqldelight.Query
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.paging3.QueryPagingSource
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.data.db.Database
import dev.avatsav.linkding.data.db.daos.PagingBookmarksDao
import dev.avatsav.linkding.data.db.sqldelight.queries.BookmarksPageBoundariesQuery
import dev.avatsav.linkding.data.model.Bookmark
import kotlinx.datetime.Instant
import me.tatarka.inject.annotations.Inject

@Inject
class SqlDelightPagingBookmarksDao(
    private val driver: SqlDriver,
    private val db: Database,
    private val dispatchers: AppCoroutineDispatchers,
) : PagingBookmarksDao {
    override fun offsetPagingSource(offset: Long, limit: Long): PagingSource<Int, Bookmark> {
        return QueryPagingSource(
            countQuery = db.bookmarksQueries.countBookmarks(),
            transacter = db.bookmarksQueries,
            context = dispatchers.io,
            queryProvider = ::queryBookmarks,
        )
    }

    override fun keyedPagingSource(anchor: Long?, limit: Long): PagingSource<Long, Bookmark> {
        return QueryPagingSource(
            transacter = db.bookmarksQueries,
            context = dispatchers.io,
            pageBoundariesProvider = this::pagedBoundariesProvider,
            queryProvider = this::keyedQuery,
        )
    }

    private fun pagedBoundariesProvider(anchor: Long?, limit: Long): Query<Long> {
        return BookmarksPageBoundariesQuery(driver, anchor, limit)
    }

    private fun keyedQuery(beginInclusive: Long, endInclusive: Long?): Query<Bookmark> {
        return db.bookmarksQueries.keyedQuery(beginInclusive, endInclusive, this::mapToBookmark)
    }

    private fun queryBookmarks(limit: Long, offset: Long): Query<Bookmark> {
        return db.bookmarksQueries.bookmarkEntries(
            limit = limit,
            offset = offset,
            mapper = this::mapToBookmark,
        )
    }

    private fun mapToBookmark(
        id: Long,
        external_id: Long,
        url: String,
        urlHost: String,
        title: String,
        description: String,
        archived: Boolean,
        unread: Boolean,
        tags: Set<String>,
        added: Instant?,
        modified: Instant?,
    ) = Bookmark(
        id = id,
        externalId = external_id,
        url = url,
        urlHost = urlHost,
        title = title,
        description = description,
        archived = archived,
        unread = unread,
        tags = tags,
        added = added,
        modified = modified,
    )
}
