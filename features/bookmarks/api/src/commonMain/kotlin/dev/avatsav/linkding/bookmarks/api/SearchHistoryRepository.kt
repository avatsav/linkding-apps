package dev.avatsav.linkding.bookmarks.api

import dev.avatsav.linkding.data.model.SearchHistory
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing search history. Handles saving, retrieving, and clearing search
 * states.
 */
interface SearchHistoryRepository {
  /**
   * Saves a search state to history. Handles duplicates by updating timestamps. Maintains a maximum
   * of 10 most recent searches.
   */
  suspend fun saveSearchHistory(searchHistory: SearchHistory)

  /** Returns a flow of the current search history, sorted by most recent first. */
  fun getSearchHistory(): Flow<List<SearchHistory>>

  /** Clears all search history. */
  suspend fun clearSearchHistory()
}
