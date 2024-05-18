package dev.avatsav.linkding.data.db.daos

import androidx.paging.PagingSource
import dev.avatsav.linkding.data.model.Bookmark

interface PagingBookmarksDao {
    fun bookmarksPagingSource(): PagingSource<Int, Bookmark>
    fun refresh(bookmarks: List<Bookmark>)
    fun append(bookmarks: List<Bookmark>)
    fun countBookmarks(): Long
}
