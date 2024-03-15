package dev.avatsav.linkding.data.db.daos

import app.cash.paging.PagingSource
import dev.avatsav.linkding.data.model.Bookmark

interface PagingBookmarksDao {
    fun offsetPagingSource(): PagingSource<Int, Bookmark>
    fun keyedPagingSource(): PagingSource<Int, Bookmark>
    fun refresh(bookmarks: List<Bookmark>)
    fun append(bookmarks: List<Bookmark>)
    fun countBookmarks(): Long
}
