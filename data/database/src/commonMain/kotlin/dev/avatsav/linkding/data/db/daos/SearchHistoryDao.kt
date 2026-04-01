package dev.avatsav.linkding.data.db.daos

import dev.avatsav.linkding.data.model.SearchHistory
import kotlinx.coroutines.flow.Flow

interface SearchHistoryDao {
  suspend fun upsert(history: SearchHistory): Long

  suspend fun insertAll(histories: List<SearchHistory>)

  fun observeRecent(limit: Long): Flow<List<SearchHistory>>

  suspend fun deleteAll()

  suspend fun pruneToLimit(limit: Long)
}
