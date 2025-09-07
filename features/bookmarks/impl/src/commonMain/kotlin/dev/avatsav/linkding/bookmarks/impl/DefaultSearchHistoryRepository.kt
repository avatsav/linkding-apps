package dev.avatsav.linkding.bookmarks.impl

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.set
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.bookmarks.api.SearchHistoryRepository
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.SearchHistory
import dev.avatsav.linkding.data.model.Tag
import dev.avatsav.linkding.inject.UserScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlin.time.Instant
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val SEARCH_HISTORY_KEY = "search-history"
private const val MAX_SEARCH_HISTORY_SIZE = 10

@OptIn(ExperimentalSettingsApi::class)
@Inject
@SingleIn(UserScope::class)
@ContributesBinding(UserScope::class)
class DefaultSearchHistoryRepository(
  private val settings: ObservableSettings,
  dispatchers: AppCoroutineDispatchers,
) : SearchHistoryRepository {

  private val flowSettings by lazy { settings.toFlowSettings(dispatchers.io) }
  private val json = Json { ignoreUnknownKeys = true }

  override suspend fun saveSearchHistory(searchHistory: SearchHistory) {
    val currentHistory = getCurrentHistory()

    val updatedHistory = currentHistory.toMutableList()
    val existingIndex = updatedHistory.indexOfFirst { it.isSameSearch(searchHistory) }

    if (existingIndex >= 0) {
      updatedHistory[existingIndex] = searchHistory
    } else {
      updatedHistory.add(0, searchHistory)
      if (updatedHistory.size > MAX_SEARCH_HISTORY_SIZE) {
        updatedHistory.removeAt(updatedHistory.lastIndex)
      }
    }
    updatedHistory.sortByDescending { it.timestamp }
    saveHistory(updatedHistory)
  }

  override fun getSearchHistory(): Flow<List<SearchHistory>> {
    return flowSettings.getStringOrNullFlow(SEARCH_HISTORY_KEY).map { json ->
      if (json.isNullOrBlank()) {
        emptyList()
      } else {
        try {
          this.json
            .decodeFromString<List<SerializableSearchState>>(json)
            .map { it.toSearchState() }
            .sortedByDescending { it.timestamp }
        } catch (e: Exception) {
          emptyList()
        }
      }
    }
  }

  override suspend fun clearSearchHistory() {
    settings.remove(SEARCH_HISTORY_KEY)
  }

  private fun getCurrentHistory(): List<SearchHistory> {
    val historyJson = settings.getStringOrNull(SEARCH_HISTORY_KEY)
    return if (historyJson.isNullOrBlank()) {
      emptyList()
    } else {
      try {
        json.decodeFromString<List<SerializableSearchState>>(historyJson).map { it.toSearchState() }
      } catch (e: Exception) {
        emptyList()
      }
    }
  }

  private fun saveHistory(history: List<SearchHistory>) {
    val serializableHistory = history.map { SerializableSearchState.fromSearchState(it) }
    val historyJson = json.encodeToString(serializableHistory)
    settings[SEARCH_HISTORY_KEY] = historyJson
  }
}

@Serializable
private data class SerializableSearchState(
  val query: String,
  val bookmarkCategory: BookmarkCategory,
  val selectedTags: List<SerializableTag>,
  val timestamp: Instant,
) {
  fun toSearchState(): SearchHistory {
    return SearchHistory(
      query = query,
      bookmarkCategory = bookmarkCategory,
      selectedTags = selectedTags.map { it.toTag() },
      timestamp = timestamp,
    )
  }

  companion object {
    fun fromSearchState(searchHistory: SearchHistory): SerializableSearchState {
      return SerializableSearchState(
        query = searchHistory.query,
        bookmarkCategory = searchHistory.bookmarkCategory,
        selectedTags = searchHistory.selectedTags.map { SerializableTag.fromTag(it) },
        timestamp = searchHistory.timestamp,
      )
    }
  }
}

@Serializable
private data class SerializableTag(val id: Long, val name: String) {
  fun toTag(): Tag = Tag(id = id, name = name)

  companion object {
    fun fromTag(tag: Tag): SerializableTag = SerializableTag(id = tag.id, name = tag.name)
  }
}
