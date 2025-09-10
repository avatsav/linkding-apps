package dev.avatsav.linkding.bookmarks.impl.daos

import app.cash.sqldelight.coroutines.asFlow
import app.cash.sqldelight.coroutines.mapToList
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.data.db.Database
import dev.avatsav.linkding.data.db.daos.SearchHistoryDao
import dev.avatsav.linkding.data.model.SearchHistory
import dev.avatsav.linkding.data.model.Tag
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class SqlDelightSearchHistoryDao(
  private val db: Database,
  private val dispatchers: AppCoroutineDispatchers,
) : SearchHistoryDao {

  override fun upsert(history: SearchHistory) {
    db.search_historyQueries.upsert(
      query = history.query,
      bookmark_category = history.bookmarkCategory,
      tags = history.selectedTags.map { it.name }.toSet(),
      timestamp = history.timestamp,
    )
  }

  override fun insertAll(histories: List<SearchHistory>) {
    db.transaction { histories.forEach { upsert(it) } }
  }

  override fun observeRecent(limit: Long): Flow<List<SearchHistory>> {
    return db.search_historyQueries.selectRecent(limit).asFlow().mapToList(dispatchers.io).map {
      list ->
      list.map { row ->
        SearchHistory(
          query = row.query,
          bookmarkCategory = row.bookmark_category,
          selectedTags = row.tags.map { Tag(id = 0L, name = it) },
          timestamp = row.timestamp,
        )
      }
    }
  }

  override fun deleteAll() {
    db.search_historyQueries.deleteAll()
  }

  override fun pruneToLimit(limit: Long) {
    db.search_historyQueries.pruneBeyond(limit)
  }
}
