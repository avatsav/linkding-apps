package dev.avatsav.linkding.bookmarks.impl

import dev.avatsav.linkding.bookmarks.api.SearchHistoryRepository
import dev.avatsav.linkding.data.db.daos.SearchHistoryDao
import dev.avatsav.linkding.data.model.SearchHistory
import dev.avatsav.linkding.inject.UserScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

private const val MaxSearchHistorySize = 20

@Inject
@SingleIn(UserScope::class)
@ContributesBinding(UserScope::class)
class DefaultSearchHistoryRepository(private val searchHistoryDao: SearchHistoryDao) :
  SearchHistoryRepository {

  override suspend fun saveSearchHistory(searchHistory: SearchHistory) {
    // Upsert the entry and update timestamp if it already exists
    searchHistoryDao.upsert(searchHistory)
    // Prune to maintain a maximum of x most recent entries
    searchHistoryDao.pruneToLimit(MaxSearchHistorySize.toLong())
  }

  override fun getSearchHistory(): Flow<List<SearchHistory>> {
    return searchHistoryDao.observeRecent(MaxSearchHistorySize.toLong())
  }

  override suspend fun clearSearchHistory() {
    searchHistoryDao.deleteAll()
  }
}
