package dev.avatsav.linkding.data.db.daos

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.data.db.Database
import dev.avatsav.linkding.data.model.SearchHistory
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class SqlDelightSearchHistoryDao(
  private val db: Database,
  private val dispatchers: AppCoroutineDispatchers,
) : SearchHistoryDao {

  override suspend fun upsert(history: SearchHistory) =
    withContext(dispatchers.io) { upsertBlocking(history) }

  override suspend fun insertAll(histories: List<SearchHistory>) =
    withContext(dispatchers.io) { db.transaction { histories.forEach(::upsertBlocking) } }

  override fun observeRecent(limit: Long): Flow<List<SearchHistory>> {
    return db.search_historyQueries.selectRecent(limit).asFlow().mapToList(dispatchers.io).map {
      list ->
      list.map { row -> SearchHistory(query = row.query, modified = row.modified) }
    }
  }

  override suspend fun deleteAll() =
    withContext(dispatchers.io) {
      val _ = db.search_historyQueries.deleteAll()
    }

  override suspend fun pruneToLimit(limit: Long) =
    withContext(dispatchers.io) {
      val _ = db.search_historyQueries.pruneBeyond(limit)
    }

  private fun upsertBlocking(history: SearchHistory) {
    val _ = db.search_historyQueries.upsert(query = history.query, modified = history.modified)
  }
}
