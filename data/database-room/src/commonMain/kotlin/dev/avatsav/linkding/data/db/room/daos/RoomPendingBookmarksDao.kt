package dev.avatsav.linkding.data.db.room.daos

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query
import androidx.room3.Update
import dev.avatsav.linkding.data.db.room.entities.PendingBookmarkEntity
import kotlinx.coroutines.flow.Flow

@Dao
abstract class RoomPendingBookmarksDao {
  @Insert abstract suspend fun insert(entity: PendingBookmarkEntity): Long

  @Update abstract suspend fun update(entity: PendingBookmarkEntity)

  @Query("SELECT * FROM pending_bookmarks WHERE accountId = :accountId ORDER BY id ASC")
  abstract fun observeAll(accountId: String): Flow<List<PendingBookmarkEntity>>

  @Query("SELECT * FROM pending_bookmarks WHERE accountId = :accountId ORDER BY id ASC")
  abstract suspend fun selectAll(accountId: String): List<PendingBookmarkEntity>

  @Query("DELETE FROM pending_bookmarks WHERE accountId = :accountId AND id = :id")
  abstract suspend fun delete(accountId: String, id: Long)
}
