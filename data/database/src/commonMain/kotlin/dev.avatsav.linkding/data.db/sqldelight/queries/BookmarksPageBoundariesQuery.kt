package dev.avatsav.linkding.data.db.sqldelight.queries

import app.cash.sqldelight.Query
import app.cash.sqldelight.db.QueryResult
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver

class BookmarksPageBoundariesQuery(
    private val driver: SqlDriver,
    private val anchor: Int?,
    private val limit: Long,
) : Query<Int>({ it.getLong(0)!!.toInt() }) {

    override fun addListener(listener: Listener) {
        driver.addListener("bookmarks", listener = listener)
    }

    override fun removeListener(listener: Listener) {
        driver.removeListener("bookmarks", listener = listener)
    }

    override fun <R> execute(mapper: (SqlCursor) -> QueryResult<R>): QueryResult<R> =
        driver.executeQuery(
            identifier = null,
            sql = """
                    |SELECT id
                    |FROM (SELECT bookmarks.id,
                    |             CASE
                    |                 WHEN ((row_number() OVER (ORDER BY bookmarks.id ASC) - 1) % :limit) = 0 THEN 1
                    |                 WHEN id = :anchor THEN 1
                    |                 ELSE 0
                    |                 END page_boundary
                    |      FROM bookmarks
                    |      ORDER BY id ASC)
                    |WHERE page_boundary = 1;
            """.trimMargin(),
            mapper = mapper,
            parameters = 2,
        ) {
            bindLong(0, limit)
            bindLong(1, anchor?.toLong())
        }

    override fun toString(): String = "Bookmarks.sq:pageBoundaries"
}
