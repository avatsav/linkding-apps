package dev.avatsav.linkding.data.db.room

import androidx.paging.PagingSource
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.avatsav.linkding.data.db.daos.RoomBookmarksDao
import dev.avatsav.linkding.data.db.daos.RoomPagingBookmarksDao
import dev.avatsav.linkding.data.db.daos.RoomSearchHistoryDao
import dev.avatsav.linkding.data.db.room.entities.BookmarkEntity
import dev.avatsav.linkding.data.db.room.entities.SearchHistoryEntity
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.SearchHistory
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import java.io.File
import kotlin.test.Test
import kotlin.time.Instant
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext

@Suppress("InjectDispatcher")
class LinkdingRoomDatabaseTest {

  @Test
  fun `update does not insert missing bookmarks`() = runTest {
    withTestDatabase { db ->
      val bookmarksDao = RoomBookmarksDao(db)
      val pagingDao = RoomPagingBookmarksDao(db)

      bookmarksDao.update(bookmark(id = 1, title = "missing"))
      pagingDao.countBookmarks() shouldBe 0

      bookmarksDao.insert(bookmark(id = 1, title = "before"))
      bookmarksDao.update(bookmark(id = 1, title = "after"))

      pagingDao.countBookmarks() shouldBe 1
      db.bookmarksQueries().selectAll().map(BookmarkEntity::title) shouldContainExactly
        listOf("after")
    }
  }

  @Test
  fun `append preserves rows and updates existing bookmarks`() = runTest {
    withTestDatabase { db ->
      val pagingDao = RoomPagingBookmarksDao(db)

      pagingDao.refresh(listOf(bookmark(id = 1, title = "one"), bookmark(id = 2, title = "two")))
      pagingDao.append(
        listOf(bookmark(id = 2, title = "two updated"), bookmark(id = 3, title = "three"))
      )

      pagingDao.countBookmarks() shouldBe 3
      pagingDao.loadBookmarks().map(Bookmark::title) shouldContainExactly
        listOf("one", "two updated", "three")
    }
  }

  @Test
  fun `search history upsert updates timestamp for existing query`() = runTest {
    withTestDatabase { db ->
      val searchHistoryDao = RoomSearchHistoryDao(db)

      searchHistoryDao.upsert(searchHistory(query = "kotlin", modified = "2026-03-14T10:00:00Z"))
      searchHistoryDao.upsert(searchHistory(query = "kotlin", modified = "2026-03-14T12:00:00Z"))

      val results = db.searchHistoryQueries().selectRecent(limit = 10)
      results.size shouldBe 1
      results.first().query shouldBe "kotlin"
      results.first().modified shouldBe Instant.parse("2026-03-14T12:00:00Z")
    }
  }

  @Test
  fun `search history emits recency order and prune limit`() = runTest {
    withTestDatabase { db ->
      val searchHistoryDao = RoomSearchHistoryDao(db)

      searchHistoryDao.upsert(searchHistory(query = "kotlin", modified = "2026-03-14T10:00:00Z"))
      searchHistoryDao.upsert(searchHistory(query = "room", modified = "2026-03-14T10:01:00Z"))

      db
        .searchHistoryQueries()
        .selectRecent(limit = 2)
        .map(SearchHistoryEntity::query) shouldContainExactly listOf("room", "kotlin")

      searchHistoryDao.pruneToLimit(1)

      db
        .searchHistoryQueries()
        .selectRecent(limit = 2)
        .map(SearchHistoryEntity::query) shouldContainExactly listOf("room")
    }
  }

  private suspend fun RoomPagingBookmarksDao.loadBookmarks(): List<Bookmark> {
    val pagingSource = bookmarksPagingSource()
    return try {
      val result =
        pagingSource.load(
          PagingSource.LoadParams.Refresh(key = null, loadSize = 50, placeholdersEnabled = false)
        )

      (result as PagingSource.LoadResult.Page).data
    } finally {
      pagingSource.invalidate()
    }
  }

  private suspend fun withTestDatabase(block: suspend (LinkdingRoomDatabase) -> Unit) {
    val dbFile = withContext(Dispatchers.IO) { File.createTempFile("linkding-room-test", ".db") }
    val db =
      Room.databaseBuilder<LinkdingRoomDatabase>(name = dbFile.absolutePath)
        .setDriver(BundledSQLiteDriver())
        .setQueryCoroutineContext(Dispatchers.IO)
        .build()

    try {
      block(db)
    } finally {
      db.close()
      dbFile.delete()
    }
  }

  private fun bookmark(id: Long, title: String) =
    Bookmark(
      id = id,
      url = "https://example.com/$id",
      urlHost = "example.com",
      title = title,
      description = "bookmark-$id",
      tags = setOf("tag-$id"),
    )

  private fun searchHistory(query: String, modified: String) =
    SearchHistory(query = query, modified = Instant.parse(modified))
}
