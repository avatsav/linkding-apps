package dev.avatsav.linkding.data.db.sqldelight.daos

import app.cash.paging.PagingSource
import app.cash.sqldelight.Query
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.paging3.QueryPagingSource
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.data.db.Database
import dev.avatsav.linkding.data.db.daos.BookmarksDao
import dev.avatsav.linkding.data.db.daos.PagingBookmarksDao
import dev.avatsav.linkding.data.db.sqldelight.queries.BookmarksPageBoundariesQuery
import dev.avatsav.linkding.data.model.Bookmark
import kotlinx.datetime.Instant
import me.tatarka.inject.annotations.Inject

@Inject
class SqlDelightPagingBookmarksDao(
    private val driver: SqlDriver,
    private val db: Database,
    private val bookmarksDao: BookmarksDao,
    private val dispatchers: AppCoroutineDispatchers,
    private val logger: Logger,
) : PagingBookmarksDao {

    override fun offsetPagingSource(): PagingSource<Int, Bookmark> {
        return QueryPagingSource(
            countQuery = db.bookmarksQueries.countBookmarks(),
            transacter = db.bookmarksQueries,
            context = dispatchers.io,
            queryProvider = ::queryBookmarks,
        )
    }

    override fun keyedPagingSource(): PagingSource<Int, Bookmark> {
        return dev.avatsav.linkding.data.db.sqldelight.QueryPagingSource(
            transacter = db.bookmarksQueries,
            context = dispatchers.io,
            pageBoundariesProvider = this::pagedBoundariesProvider,
            queryProvider = this::keyedQuery,
            logger = logger,
        )
    }

    override fun refresh(bookmarks: List<Bookmark>) {
        db.transaction {
            bookmarksDao.deleteAll()
            bookmarksDao.insert(bookmarks)
        }
    }

    override fun append(bookmarks: List<Bookmark>) {
        bookmarksDao.upsert(bookmarks)
    }

    private fun pagedBoundariesProvider(anchor: Int?, limit: Long): Query<Int> {
        return BookmarksPageBoundariesQuery(driver, anchor, limit)
    }

    private fun keyedQuery(beginInclusive: Int, endInclusive: Int?): Query<Bookmark> {
        return db.bookmarksQueries.keyedQuery(
            beginInclusive.toLong(),
            endInclusive?.toLong(),
            this::mapToBookmark,
        )
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
        linkding_id: Long,
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
        localId = id,
        id = linkding_id,
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
