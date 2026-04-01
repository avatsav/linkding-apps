package dev.avatsav.linkding.data.db.room

import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import androidx.room3.TypeConverters
import dev.avatsav.linkding.data.db.room.converters.LinkdingRoomConverters
import dev.avatsav.linkding.data.db.room.daos.RoomBookmarksDao
import dev.avatsav.linkding.data.db.room.daos.RoomSearchHistoryDao
import dev.avatsav.linkding.data.db.room.entities.BookmarkEntity
import dev.avatsav.linkding.data.db.room.entities.SearchHistoryEntity

@Database(
  entities = [BookmarkEntity::class, SearchHistoryEntity::class],
  version = 1,
  exportSchema = true,
)
@TypeConverters(LinkdingRoomConverters::class)
@ConstructedBy(LinkdingRoomDatabaseConstructor::class)
abstract class LinkdingRoomDatabase : RoomDatabase() {

  abstract fun bookmarksQueries(): RoomBookmarksDao

  abstract fun searchHistoryQueries(): RoomSearchHistoryDao
}

@Suppress("KotlinNoActualForExpect")
expect object LinkdingRoomDatabaseConstructor : RoomDatabaseConstructor<LinkdingRoomDatabase> {
  override fun initialize(): LinkdingRoomDatabase
}
