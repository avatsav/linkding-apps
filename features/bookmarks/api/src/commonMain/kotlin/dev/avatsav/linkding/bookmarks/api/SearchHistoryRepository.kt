package dev.avatsav.linkding.bookmarks.api

import dev.avatsav.linkding.data.model.SearchHistory
import kotlinx.coroutines.flow.Flow

interface SearchHistoryRepository {
  suspend fun saveSearchHistory(searchHistory: SearchHistory)

  fun getSearchHistory(): Flow<List<SearchHistory>>

  suspend fun clearSearchHistory()
}
