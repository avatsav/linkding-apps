package dev.avatsav.linkding.data.db.room

import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.OnConflictStrategy
import androidx.room3.Query
import androidx.room3.Transaction
import kotlinx.coroutines.flow.Flow
import kotlin.time.Instant

@Dao
abstract class RoomSearchHistoryQueries {
  @Insert(onConflict = OnConflictStrategy.IGNORE)
  protected abstract suspend fun insertIgnore(history: SearchHistoryEntity): Long

  @Query("UPDATE search_history SET modified = :modified WHERE query = :query")
  protected abstract suspend fun updateModified(query: String, modified: Instant): Int

  @Query("SELECT * FROM search_history ORDER BY modified DESC LIMIT :limit")
  abstract fun observeRecent(limit: Long): Flow<List<SearchHistoryEntity>>

  @Query("SELECT * FROM search_history ORDER BY modified DESC LIMIT :limit")
  abstract suspend fun selectRecent(limit: Long): List<SearchHistoryEntity>

  @Query("DELETE FROM search_history")
  abstract suspend fun deleteAll()

  @Query(
    "DELETE FROM search_history WHERE id NOT IN (SELECT id FROM search_history ORDER BY modified DESC LIMIT :limit)"
  )
  abstract suspend fun pruneToLimit(limit: Long)

  @Transaction
  open suspend fun upsert(history: SearchHistoryEntity) {
    val _ = updateModified(query = history.query, modified = history.modified)
    val ignoredInsert = insertIgnore(history)
  }

  @Transaction
  open suspend fun insertAll(histories: List<SearchHistoryEntity>) {
    for (history in histories) {
      upsert(history)
    }
  }
}
