package dev.avatsav.linkding.data.db.room

import androidx.room3.Entity
import androidx.room3.Index
import androidx.room3.PrimaryKey
import dev.avatsav.linkding.data.model.SearchHistory
import kotlin.time.Instant

@Entity(
  tableName = "search_history",
  indices = [Index(value = ["query"], unique = true)],
)
data class SearchHistoryEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val query: String,
  val modified: Instant,
)

fun SearchHistoryEntity.toModel(): SearchHistory = SearchHistory(query = query, modified = modified)

fun SearchHistory.toRoomEntity(): SearchHistoryEntity = SearchHistoryEntity(query = query, modified = modified)
