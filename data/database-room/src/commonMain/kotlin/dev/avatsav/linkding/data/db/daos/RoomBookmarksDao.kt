package dev.avatsav.linkding.data.db.daos

import dev.avatsav.linkding.data.db.room.LinkdingRoomDatabase
import dev.avatsav.linkding.data.db.room.toRoomEntity
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class RoomBookmarksDao(private val db: LinkdingRoomDatabase) : BookmarksDao {
  override suspend fun insert(bookmark: dev.avatsav.linkding.data.model.Bookmark) {
    db.bookmarksQueries().insertAll(listOf(bookmark.toRoomEntity()))
  }

  override suspend fun insert(bookmarks: List<dev.avatsav.linkding.data.model.Bookmark>) {
    db.bookmarksQueries().insertAll(bookmarks.map { it.toRoomEntity() })
  }

  override suspend fun update(bookmark: dev.avatsav.linkding.data.model.Bookmark) {
    db.bookmarksQueries().update(bookmark.toRoomEntity())
  }

  override suspend fun upsert(bookmark: dev.avatsav.linkding.data.model.Bookmark) {
    db.bookmarksQueries().upsert(bookmark.toRoomEntity())
  }

  override suspend fun upsert(bookmarks: List<dev.avatsav.linkding.data.model.Bookmark>) {
    db.bookmarksQueries().upsertAll(bookmarks.map { it.toRoomEntity() })
  }

  override suspend fun delete(id: Long) {
    db.bookmarksQueries().deleteByLinkdingId(id)
  }

  override suspend fun deleteAll() {
    db.bookmarksQueries().deleteAll()
  }
}
