package dev.avatsav.linkding.data.db.daos

import dev.avatsav.linkding.data.model.Bookmark

interface BookmarksDao {
  fun insert(bookmark: Bookmark)

  fun insert(bookmarks: List<Bookmark>)

  fun update(bookmark: Bookmark)

  fun upsert(bookmark: Bookmark)

  fun upsert(bookmarks: List<Bookmark>)

  fun delete(id: Long)

  fun deleteAll()
}
