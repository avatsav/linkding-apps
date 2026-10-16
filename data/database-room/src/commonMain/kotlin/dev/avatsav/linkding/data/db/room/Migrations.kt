package dev.avatsav.linkding.data.db.room

import androidx.room3.migration.Migration
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.execSQL

val MIGRATION_1_2 =
  object : Migration(1, 2) {
    override suspend fun migrate(connection: SQLiteConnection) {
      connection.execSQL(
        """
      CREATE TABLE IF NOT EXISTS `pending_bookmarks` (
        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
        `accountId` TEXT NOT NULL,
        `url` TEXT NOT NULL,
        `title` TEXT,
        `description` TEXT,
        `notes` TEXT,
        `tags` TEXT NOT NULL,
        `archived` INTEGER NOT NULL,
        `unread` INTEGER NOT NULL,
        `shared` INTEGER NOT NULL,
        `createdAt` TEXT NOT NULL,
        `state` TEXT NOT NULL,
        `lastError` TEXT
      )
    """
      )
    }
  }
