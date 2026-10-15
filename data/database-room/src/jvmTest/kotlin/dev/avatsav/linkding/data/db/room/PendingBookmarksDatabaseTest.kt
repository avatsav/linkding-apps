package dev.avatsav.linkding.data.db.room

import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import dev.avatsav.linkding.data.db.daos.RoomPendingBookmarksDao
import dev.avatsav.linkding.data.model.PendingBookmark
import dev.avatsav.linkding.data.model.SaveBookmark
import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*

@Suppress("InjectDispatcher")
class PendingBookmarksDatabaseTest {
  @Test
  fun `pending fields survive database reopen and feed refresh`() = runTest {
    val file = File.createTempFile("pending-bookmarks", ".db")
    try {
      var db = open(file)
      val dao = RoomPendingBookmarksDao(db)
      val original =
          PendingBookmark(
              bookmark =
                  SaveBookmark(
                      "https://example.com",
                      "Title",
                      "Description",
                      "Notes",
                      setOf("a", "b"),
                      true,
                      true,
                      true,
                  ),
              createdAt = Instant.parse("2026-03-14T10:00:00Z"),
          )
      val id = dao.insert("account-a", original)
      val edited =
          original.copy(
              id = id,
              bookmark = original.bookmark.copy(title = "Edited", notes = "New notes"),
          )
      dao.update("account-a", edited)
      db.bookmarksQueries().replaceAll(emptyList())
      db.close()
      db = open(file)
      try {
        val reopened = RoomPendingBookmarksDao(db)
        assertEquals(listOf(edited), reopened.observeAll("account-a").first())
        assertTrue(reopened.getAll("account-b").isEmpty())
        reopened.update("account-b", edited.copy(lastError = "wrong account"))
        reopened.delete("account-b", id)
        assertEquals(listOf(edited), reopened.getAll("account-a"))
        reopened.delete("account-a", id)
        assertTrue(reopened.getAll("account-a").isEmpty())
      } finally {
        db.close()
      }
    } finally {
      file.delete()
    }
  }

  @Test
  fun `version one migration preserves existing data and validates pending schema`() = runTest {
    val file = File.createTempFile("pending-migration", ".db")
    try {
      // Build the old database from its checked-in schema, not the current generated code.
      val schemaFile = File("schemas/dev.avatsav.linkding.data.db.room.LinkdingRoomDatabase/1.json")
      val schema =
          Json.parseToJsonElement(schemaFile.readText()).jsonObject.getValue("database").jsonObject
      BundledSQLiteDriver().open(file.absolutePath).use { connection ->
        schema.getValue("entities").jsonArray.forEach { element ->
          val entity = element.jsonObject
          val name = entity.getValue("tableName").jsonPrimitive.content
          connection.execSQL(
              entity.getValue("createSql").jsonPrimitive.content.replace("\${TABLE_NAME}", name)
          )
          entity.getValue("indices").jsonArray.forEach { index ->
            connection.execSQL(
                index.jsonObject
                    .getValue("createSql")
                    .jsonPrimitive
                    .content
                    .replace("\${TABLE_NAME}", name)
            )
          }
        }
        schema.getValue("setupQueries").jsonArray.forEach {
          connection.execSQL(it.jsonPrimitive.content)
        }
        connection.execSQL(
            "INSERT INTO search_history (query, modified) VALUES ('retained', '2026-03-14T10:00:00Z')"
        )
        connection.execSQL("PRAGMA user_version = 1")
      }
      val db = open(file)
      try {
        assertEquals("retained", db.searchHistoryQueries().selectRecent(1).single().query)
        val dao = RoomPendingBookmarksDao(db)
        val id =
            dao.insert(
                "account",
                PendingBookmark(
                    bookmark = SaveBookmark("https://example.com"),
                    createdAt = Instant.parse("2026-03-14T10:00:00Z"),
                ),
            )
        assertEquals(id, dao.getAll("account").single().id)
      } finally {
        db.close()
      }
    } finally {
      file.delete()
    }
  }

  private fun open(file: File) =
      Room.databaseBuilder<LinkdingRoomDatabase>(name = file.absolutePath)
          .addMigrations(MIGRATION_1_2)
          .setDriver(BundledSQLiteDriver())
          .setQueryCoroutineContext(Dispatchers.IO)
          .build()
}
