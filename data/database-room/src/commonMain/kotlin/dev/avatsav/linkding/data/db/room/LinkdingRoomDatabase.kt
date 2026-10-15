package dev.avatsav.linkding.data.db.room

import androidx.room3.ColumnTypeConverters
import androidx.room3.ConstructedBy
import androidx.room3.Database
import androidx.room3.RoomDatabase
import androidx.room3.RoomDatabaseConstructor
import dev.avatsav.linkding.data.db.room.converters.LinkdingRoomConverters
import dev.avatsav.linkding.data.db.room.daos.RoomBookmarksDao
import dev.avatsav.linkding.data.db.room.daos.RoomPendingBookmarksDao
import dev.avatsav.linkding.data.db.room.daos.RoomSearchHistoryDao
import dev.avatsav.linkding.data.db.room.entities.BookmarkEntity
import dev.avatsav.linkding.data.db.room.entities.PendingBookmarkEntity
import dev.avatsav.linkding.data.db.room.entities.SearchHistoryEntity

@Database(
    entities = [BookmarkEntity::class, SearchHistoryEntity::class, PendingBookmarkEntity::class],
    version = 2,
    exportSchema = true,
)
@ColumnTypeConverters(LinkdingRoomConverters::class)
@ConstructedBy(LinkdingRoomDatabaseConstructor::class)
abstract class LinkdingRoomDatabase : RoomDatabase() {

  abstract fun bookmarksQueries(): RoomBookmarksDao

  abstract fun searchHistoryQueries(): RoomSearchHistoryDao

  abstract fun pendingBookmarksQueries(): RoomPendingBookmarksDao
}

@Suppress("KotlinNoActualForExpect")
expect object LinkdingRoomDatabaseConstructor : RoomDatabaseConstructor<LinkdingRoomDatabase> {
  override fun initialize(): LinkdingRoomDatabase
}
