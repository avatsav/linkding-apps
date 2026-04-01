package dev.avatsav.linkding.data.db.daos

import dev.avatsav.linkding.data.model.Bookmark

interface BookmarksDao {
  suspend fun insert(bookmark: Bookmark)

  suspend fun insert(bookmarks: List<Bookmark>)

  suspend fun update(bookmark: Bookmark)

  suspend fun upsert(bookmark: Bookmark): Long

  suspend fun upsert(bookmarks: List<Bookmark>): List<Long>

  suspend fun delete(id: Long)

  suspend fun deleteAll()
}
