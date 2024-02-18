package dev.avatsav.linkding.data.db.daos

import app.cash.paging.PagingSource
import dev.avatsav.linkding.data.model.Bookmark

interface PagingBookmarksDao {
    fun offsetPagingSource(offset: Long, limit: Long): PagingSource<Int, Bookmark>
    fun keyedPagingSource(anchor: Long?, limit: Long): PagingSource<Long, Bookmark>
}
