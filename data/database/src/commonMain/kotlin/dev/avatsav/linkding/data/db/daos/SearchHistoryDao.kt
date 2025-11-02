package dev.avatsav.linkding.data.db.daos

import dev.avatsav.linkding.data.model.SearchHistory
import kotlinx.coroutines.flow.Flow

interface SearchHistoryDao {
  fun upsert(history: SearchHistory)

  fun insertAll(histories: List<SearchHistory>)

  fun observeRecent(limit: Long): Flow<List<SearchHistory>>

  fun deleteAll()

  fun pruneToLimit(limit: Long)
}
