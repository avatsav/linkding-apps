package dev.avatsav.linkding.data.db.daos

import dev.avatsav.linkding.data.db.room.LinkdingRoomDatabase
import dev.avatsav.linkding.data.db.room.entities.toModel
import dev.avatsav.linkding.data.db.room.entities.toRoomEntity
import dev.avatsav.linkding.data.model.PendingBookmark
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
@ContributesBinding(AppScope::class)
class RoomPendingBookmarksDao(private val db: LinkdingRoomDatabase) : PendingBookmarksDao {
  override fun observeAll(accountId: String): Flow<List<PendingBookmark>> =
    db.pendingBookmarksQueries().observeAll(accountId).map { rows -> rows.map { it.toModel() } }

  override suspend fun getAll(accountId: String): List<PendingBookmark> =
    db.pendingBookmarksQueries().selectAll(accountId).map { it.toModel() }

  override suspend fun insert(accountId: String, bookmark: PendingBookmark): Long =
    db.pendingBookmarksQueries().insert(bookmark.toRoomEntity(accountId))

  override suspend fun update(accountId: String, bookmark: PendingBookmark) {
    // Do not allow an update to move a row between accounts.
    if (getAll(accountId).any { it.id == bookmark.id }) {
      db.pendingBookmarksQueries().update(bookmark.toRoomEntity(accountId))
    }
  }

  override suspend fun delete(accountId: String, id: Long) =
    db.pendingBookmarksQueries().delete(accountId, id)
}
