package dev.avatsav.linkding.data.db.daos

import dev.avatsav.linkding.data.db.room.LinkdingRoomDatabase
import dev.avatsav.linkding.data.db.room.toModel
import dev.avatsav.linkding.data.db.room.toRoomEntity
import dev.avatsav.linkding.data.model.SearchHistory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class RoomSearchHistoryDao(private val db: LinkdingRoomDatabase) : SearchHistoryDao {
  override suspend fun upsert(history: SearchHistory) {
    db.searchHistoryQueries().upsert(history.toRoomEntity())
  }

  override suspend fun insertAll(histories: List<SearchHistory>) {
    db.searchHistoryQueries().insertAll(histories.map { it.toRoomEntity() })
  }

  override fun observeRecent(limit: Long): Flow<List<SearchHistory>> =
    db.searchHistoryQueries().observeRecent(limit).map { histories -> histories.map { it.toModel() } }

  override suspend fun deleteAll() {
    db.searchHistoryQueries().deleteAll()
  }

  override suspend fun pruneToLimit(limit: Long) {
    db.searchHistoryQueries().pruneToLimit(limit)
  }
}
