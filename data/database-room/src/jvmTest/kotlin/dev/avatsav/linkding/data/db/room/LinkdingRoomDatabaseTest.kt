package dev.avatsav.linkding.data.db.room

import androidx.paging.PagingConfig
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.room3.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import dev.avatsav.linkding.data.db.daos.RoomBookmarksDao
import dev.avatsav.linkding.data.db.daos.RoomPagingBookmarksDao
import dev.avatsav.linkding.data.db.daos.RoomSearchHistoryDao
import dev.avatsav.linkding.data.db.room.entities.BookmarkEntity
import dev.avatsav.linkding.data.db.room.entities.SearchHistoryEntity
import dev.avatsav.linkding.data.db.room.entities.toModel
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.SearchHistory
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import java.io.File
import kotlin.test.Test
import kotlin.time.Duration.Companion.seconds
import kotlin.time.Instant
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

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
        listOf("three", "two updated", "one")
    }
  }

  @Test
  fun `uploaded bookmark invalidates the feed and enters its first page`() = runTest {
    withTestDatabase { db ->
      val pagingDao = RoomPagingBookmarksDao(db)
      val bookmarksDao = RoomBookmarksDao(db)
      pagingDao.refresh(
        (40L downTo 1L).map { id ->
          bookmark(id, "Cached $id").copy(added = Instant.fromEpochSeconds(1_700_000_000 + id))
        }
      )
      val activeSource = pagingDao.bookmarksPagingSource()
      val invalidated = CompletableDeferred<Unit>()
      activeSource.registerInvalidatedCallback { invalidated.complete(Unit) }
      val initialPage =
        activeSource.load(
          PagingSource.LoadParams.Refresh(
            key = null,
            loadSize = 20,
            placeholdersEnabled = false,
          )
        ) as PagingSource.LoadResult.Page
      val uploaded =
        bookmark(41, "Just uploaded").copy(added = Instant.fromEpochSeconds(1_700_000_041))
      bookmarksDao.upsert(uploaded)
      // The existing feed must react to the local write, without a backend refresh.
      withContext(Dispatchers.Default) { withTimeout(5.seconds) { invalidated.await() } }
      val refreshKey =
        activeSource.getRefreshKey(
          PagingState(
            pages = listOf(initialPage),
            anchorPosition = 0,
            config = PagingConfig(pageSize = 20, initialLoadSize = 20),
            leadingPlaceholderCount = 0,
          )
        )
      pagingDao.loadBookmarks(loadSize = 20, key = refreshKey).first().id shouldBe uploaded.id
      pagingDao.countBookmarks() shouldBe 41
    }
  }

  @Test
  fun `feed sorts server timestamps numerically with server id as a stable tie breaker`() =
    runTest {
      withTestDatabase { db ->
        val pagingDao = RoomPagingBookmarksDao(db)
        val wholeSecond = Instant.parse("2026-03-14T10:00:00Z")
        val halfSecond = Instant.parse("2026-03-14T10:00:00.500Z")
        pagingDao.refresh(
          listOf(
            bookmark(300, "Whole second").copy(added = wholeSecond),
            bookmark(100, "Half second").copy(added = halfSecond),
            bookmark(500, "Older import").copy(added = Instant.parse("2026-03-13T10:00:00Z")),
            bookmark(101, "Same timestamp").copy(added = halfSecond),
          )
        )
        pagingDao.loadBookmarks().map(Bookmark::id) shouldContainExactly
          listOf(101L, 100L, 300L, 500L)
      }
    }

  @Test
  fun `refreshing the first remote page keeps the visible paging window at the top`() = runTest {
    withTestDatabase { db ->
      val pagingDao = RoomPagingBookmarksDao(db)
      val serverRows =
        (40L downTo 1L).map { id ->
          bookmark(id, "Bookmark $id").copy(added = Instant.fromEpochSeconds(1_700_000_000 + id))
        }
      pagingDao.refresh(serverRows)
      val source = pagingDao.bookmarksPagingSource()
      val initialPage =
        source.load(
          PagingSource.LoadParams.Refresh(key = null, loadSize = 20, placeholdersEnabled = true)
        ) as PagingSource.LoadResult.Page
      val state =
        PagingState(
          pages = listOf(initialPage),
          anchorPosition = 0,
          config = PagingConfig(pageSize = 20, initialLoadSize = 20),
          leadingPlaceholderCount = 0,
        )
      // RemoteMediator refresh replaces the cache with the first server page.
      pagingDao.refresh(serverRows.take(20))
      val refreshKey = source.getRefreshKey(state)
      val refreshed = pagingDao.loadBookmarks(loadSize = 20, key = refreshKey)
      refreshed.map(Bookmark::id) shouldContainExactly initialPage.data.map(Bookmark::id)
      source.invalidate()
    }
  }

  @Test
  fun `appending a server page keeps the first visible paging window unchanged`() = runTest {
    withTestDatabase { db ->
      val pagingDao = RoomPagingBookmarksDao(db)
      val config = PagingConfig(pageSize = 20)
      fun row(id: Long) =
        bookmark(id, "Bookmark $id").copy(added = Instant.fromEpochSeconds(1_700_000_000 + id))
      pagingDao.refresh((140L downTo 21L).map(::row))
      val source = pagingDao.bookmarksPagingSource()
      val initialPage =
        source.load(
          PagingSource.LoadParams.Refresh(
            key = null,
            loadSize = config.initialLoadSize,
            placeholdersEnabled = true,
          )
        ) as PagingSource.LoadResult.Page
      pagingDao.append((20L downTo 1L).map(::row))
      val refreshKey =
        source.getRefreshKey(
          PagingState(
            pages = listOf(initialPage),
            anchorPosition = 0,
            config = config,
            leadingPlaceholderCount = 0,
          )
        )
      val refreshed = pagingDao.loadBookmarks(loadSize = config.initialLoadSize, key = refreshKey)
      refreshed.map(Bookmark::id) shouldContainExactly initialPage.data.map(Bookmark::id)
      source.invalidate()
    }
  }

  @Test
  fun `mapped feed matches Room refresh keys at top and scrolled anchors`() = runTest {
    withTestDatabase { db ->
      val pagingDao = RoomPagingBookmarksDao(db)
      pagingDao.refresh((200L downTo 1L).map { bookmark(it, "Bookmark $it") })
      val source = db.bookmarksQueries().bookmarksPagingSource()
      val mappedSource = pagingDao.bookmarksPagingSource()
      val config = PagingConfig(pageSize = 20)
      for (offset in listOf(0, 60)) {
        val page =
          source.load(
            PagingSource.LoadParams.Refresh(
              key = offset,
              loadSize = config.initialLoadSize,
              placeholdersEnabled = true,
            )
          ) as PagingSource.LoadResult.Page
        val mappedPage =
          PagingSource.LoadResult.Page(
            data = page.data.map { it.toModel() },
            prevKey = page.prevKey,
            nextKey = page.nextKey,
            itemsBefore = page.itemsBefore,
            itemsAfter = page.itemsAfter,
          )
        for (anchor in listOf(null, 0, 29, 30, 59, 80, 130)) {
          val originalState =
            PagingState(
              pages = listOf(page),
              anchorPosition = anchor,
              config = config,
              leadingPlaceholderCount = page.itemsBefore,
            )
          val mappedState =
            PagingState(
              pages = listOf(mappedPage),
              anchorPosition = anchor,
              config = config,
              leadingPlaceholderCount = page.itemsBefore,
            )
          mappedSource.getRefreshKey(mappedState) shouldBe source.getRefreshKey(originalState)
        }
      }
      source.invalidate()
      mappedSource.invalidate()
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

  private suspend fun RoomPagingBookmarksDao.loadBookmarks(
    loadSize: Int = 50,
    key: Int? = null,
  ): List<Bookmark> {
    val pagingSource = bookmarksPagingSource()
    return try {
      val result =
        pagingSource.load(
          PagingSource.LoadParams.Refresh(
            key = key,
            loadSize = loadSize,
            placeholdersEnabled = false,
          )
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
