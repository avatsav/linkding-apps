package dev.avatsav.linkding.data.db.room

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import androidx.room3.TypeConverters

@Database(
  entities = [BookmarkEntity::class, SearchHistoryEntity::class],
  version = 1,
  exportSchema = false,
)
@TypeConverters(LinkdingRoomConverters::class)
@ConstructedBy(LinkdingRoomDatabaseConstructor::class)
abstract class LinkdingRoomDatabase : RoomDatabase() {
  abstract fun bookmarksQueries(): RoomBookmarksQueries

  abstract fun searchHistoryQueries(): RoomSearchHistoryQueries
}

@Suppress("KotlinNoActualForExpect")
expect object LinkdingRoomDatabaseConstructor : RoomDatabaseConstructor<LinkdingRoomDatabase> {
  override fun initialize(): LinkdingRoomDatabase
}
