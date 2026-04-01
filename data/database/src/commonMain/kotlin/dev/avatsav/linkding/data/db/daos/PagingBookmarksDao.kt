package dev.avatsav.linkding.data.db.daos

import androidx.paging.PagingSource
import dev.avatsav.linkding.data.model.Bookmark

interface PagingBookmarksDao {
  fun bookmarksPagingSource(): PagingSource<Int, Bookmark>

  suspend fun refresh(bookmarks: List<Bookmark>)

  suspend fun append(bookmarks: List<Bookmark>)

  suspend fun countBookmarks(): Long
}
