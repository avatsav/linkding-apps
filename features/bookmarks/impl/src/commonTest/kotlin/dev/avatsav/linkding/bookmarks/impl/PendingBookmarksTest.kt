package dev.avatsav.linkding.bookmarks.impl

import com.github.michaelbull.result.Err
import com.github.michaelbull.result.Ok
import com.github.michaelbull.result.Result
import dev.avatsav.linkding.api.LinkdingBookmarksApi
import dev.avatsav.linkding.api.models.*
import dev.avatsav.linkding.bookmarks.impl.mappers.BookmarkMapper
import dev.avatsav.linkding.data.db.daos.BookmarksDao
import dev.avatsav.linkding.data.db.daos.PendingBookmarksDao
import dev.avatsav.linkding.data.model.*
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.internet.ConnectivityObserver
import dev.avatsav.linkding.prefs.AppPreferences
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

class PendingBookmarksTest {
  @Test
  fun `save finishes locally without waiting for an unreachable backend`() = runTest {
    val f = Fixture()
    f.api.onCheck = { awaitCancellation() }
    val save = async { f.queue.create(draft) }
    runCurrent()
    assertTrue(
        save.isCompleted,
        "Save must finish after the database write, not after a network timeout",
    )
    assertTrue(save.await().isOk)
    assertEquals(draft, f.queue.observeFirst().bookmark)
    assertTrue(f.api.checkedUrls.isEmpty(), "Create must leave all network work to the uploader")
  }

  @Test
  fun `new local save does not wait behind an uploader checking the host`() = runTest {
    val f = Fixture()
    f.online.value = false
    assertTrue(f.queue.create(draft).isOk)
    f.api.onCheck = { awaitCancellation() }
    backgroundScope.launch { f.queue.retry() }
    runCurrent()
    val another = draft.copy(url = "https://example.com/another")
    val save = async { f.queue.create(another) }
    runCurrent()
    assertTrue(save.isCompleted, "An uploader network wait must not block a local save")
    assertTrue(save.await().isOk)
    assertEquals(another, f.queue.get(2)?.bookmark)
  }

  @Test
  fun `unreachable host queues without POST`() = runTest {
    val fixture = Fixture()
    assertTrue(fixture.queue.create(draft).isOk)
    fixture.queue.retry()
    assertEquals(draft, fixture.store.rows.value.single().second.bookmark)
    assertEquals(PendingBookmark.State.Pending, fixture.store.rows.value.single().second.state)
    assertTrue(fixture.api.sent.isEmpty())
  }

  @Test
  fun `editing updates the durable creation and retry sends latest fields`() = runTest {
    val f = Fixture()
    assertTrue(f.queue.create(draft).isOk)
    val id = f.queue.observeFirst().id
    val changed = draft.copy(title = "Updated", notes = "Offline notes", tags = setOf("new"))
    assertTrue(f.queue.edit(id, changed))
    // Reconstruct the queue to model a new authenticated session after process restart.
    val restarted = f.newQueue()
    f.api.online = true
    restarted.retry()
    assertEquals("Updated", f.api.sent.single().title)
    assertEquals("Offline notes", f.api.sent.single().notes)
    assertEquals(setOf("new"), f.api.sent.single().tagNames)
    assertTrue(f.store.rows.value.isEmpty())
    assertEquals("Updated", f.cache.rows.single().title)
  }

  @Test
  fun `discard never calls the server`() = runTest {
    val f = Fixture()
    assertTrue(f.queue.create(draft).isOk)
    f.queue.discard(f.queue.observeFirst().id)
    f.api.online = true
    f.queue.retry()
    assertTrue(f.store.rows.value.isEmpty())
    assertTrue(f.api.sent.isEmpty())
  }

  @Test
  fun `authentication and validation rejections remain actionable on the pending item`() = runTest {
    for (error in listOf(LinkdingError.Unauthorized(), LinkdingError.Other("Invalid URL"))) {
      val f = Fixture()
      f.api.checkError = error
      assertTrue(f.queue.create(draft).isOk)
      f.queue.retry()
      val pending = f.queue.observeFirst()
      assertEquals(draft, pending.bookmark)
      assertEquals(PendingBookmark.State.Failed, pending.state)
      assertEquals(error.message, pending.lastError)
      assertTrue(f.api.sent.isEmpty())
    }
  }

  @Test
  fun `POST rejection keeps the saved fields with an actionable failure`() = runTest {
    val f = Fixture()
    f.api.online = true
    f.api.saveError = LinkdingError.Other("Invalid fields")
    assertTrue(f.queue.create(draft).isOk)
    f.queue.retry()
    assertEquals(draft, f.queue.observeFirst().bookmark)
    assertEquals(PendingBookmark.State.Failed, f.queue.observeFirst().state)
    assertEquals("Invalid fields", f.queue.observeFirst().lastError)
  }

  @Test
  fun `ambiguous POST is retained and existing URL is not posted again`() = runTest {
    val f = Fixture()
    f.api.online = true
    f.api.saveError = LinkdingError.Connectivity("Timed out")
    assertTrue(f.queue.create(draft).isOk)
    f.queue.retry()
    assertEquals(PendingBookmark.State.Uncertain, f.queue.observeFirst().state)
    f.api.existing = remote
    f.queue.retry()
    assertEquals(1, f.api.sent.size)
    assertEquals(PendingBookmark.State.Failed, f.queue.observeFirst().state)
  }

  @Test
  fun `invalid success response also preserves an uncertain creation`() = runTest {
    val f = Fixture()
    f.api.online = true
    f.api.saveError = LinkdingError.Other("Invalid response", requestMayHaveSucceeded = true)
    assertTrue(f.queue.create(draft).isOk)
    f.queue.retry()
    assertEquals(PendingBookmark.State.Uncertain, f.queue.observeFirst().state)
  }

  @Test
  fun `uploader cancellation propagates and leaves a durable in-flight record`() = runTest {
    val f = Fixture()
    f.api.online = true
    f.api.onSave = { throw CancellationException("Stopped") }
    assertTrue(f.queue.create(draft).isOk)
    try {
      f.queue.retry()
      error("Cancellation should propagate")
    } catch (_: CancellationException) {
      assertEquals(PendingBookmark.State.Uncertain, f.queue.observeFirst().state)
    }
  }

  @Test
  fun `foreground startup uploads pending items without an internet hint`() = runTest {
    val f = Fixture()
    f.online.value = false
    assertTrue(f.queue.create(draft).isOk)
    f.api.online = true
    val uploader = backgroundScope.launch { f.newQueue().runUploader() }
    runCurrent()
    assertEquals(1, f.api.sent.size)
    assertTrue(f.store.rows.value.isEmpty())
    uploader.cancel()
  }

  @Test
  fun `foreground retry discovers host recovery without a connectivity change`() = runTest {
    val f = Fixture()
    f.online.value = false
    assertTrue(f.queue.create(draft).isOk)
    backgroundScope.launch { f.queue.runUploader() }
    runCurrent()
    assertTrue(f.api.sent.isEmpty())
    f.api.online = true
    advanceTimeBy(60_000)
    runCurrent()
    assertEquals(1, f.api.sent.size)
    assertTrue(f.store.rows.value.isEmpty())
  }

  @Test
  fun `startup does not automatically resend an interrupted POST`() = runTest {
    val f = Fixture()
    assertTrue(f.queue.create(draft).isOk)
    val item = f.queue.observeFirst()
    f.store.update("account", item.copy(state = PendingBookmark.State.Uploading))
    f.api.online = true
    backgroundScope.launch { f.newQueue().runUploader() }
    runCurrent()
    assertEquals(PendingBookmark.State.Uncertain, f.queue.observeFirst().state)
    assertTrue(f.api.sent.isEmpty())
  }

  @Test
  fun `editing an active POST is rejected immediately instead of blocking`() = runTest {
    val f = Fixture()
    assertTrue(f.queue.create(draft).isOk)
    val id = f.queue.observeFirst().id
    f.api.online = true
    val started = CompletableDeferred<Unit>()
    val release = CompletableDeferred<Unit>()
    f.api.onSave = {
      started.complete(Unit)
      release.await()
    }
    val retry = launch { f.queue.retry() }
    started.await()
    val edit = async { f.queue.edit(id, draft.copy(title = "Too late")) }
    runCurrent()
    assertTrue(edit.isCompleted, "An active POST must reject the edit without a network wait")
    assertFalse(edit.await())
    release.complete(Unit)
    retry.join()
    assertTrue(f.store.rows.value.isEmpty())
  }

  @Test
  fun `old session cannot upload after credentials change and new account cannot see drafts`() =
      runTest {
        val f = Fixture()
        assertTrue(f.queue.create(draft).isOk)
        f.preferences.localAccountId = "other-account"
        f.preferences.config = ApiConfig("https://other.example", "other-key")
        f.api.online = true
        f.queue.retry()
        assertTrue(f.api.sent.isEmpty())
        assertEquals(1, f.store.rows.value.size)
        val otherQueue = f.newQueue(f.preferences.config!!)
        assertEquals(null, otherQueue.get(f.store.rows.value.single().second.id))
        otherQueue.retry()
        assertTrue(f.api.sent.isEmpty())
      }

  @Test
  fun `edit during preflight returns immediately and POST uses the latest fields`() = runTest {
    val f = Fixture()
    assertTrue(f.queue.create(draft).isOk)
    f.api.online = true
    val release = CompletableDeferred<Unit>()
    f.api.onCheck = { release.await() }
    val retry = backgroundScope.launch { f.queue.retry() }
    runCurrent()
    val changed = draft.copy(title = "Latest", notes = "New notes", tags = setOf("edited"))
    val edit = async { f.queue.edit(1, changed) }
    runCurrent()
    assertTrue(edit.isCompleted, "An in-flight URL check must not block a local edit")
    assertTrue(edit.await())
    assertEquals(changed, f.queue.observeFirst().bookmark)
    release.complete(Unit)
    retry.join()
    assertEquals("Latest", f.api.sent.single().title)
    assertEquals("New notes", f.api.sent.single().notes)
    assertEquals(setOf("edited"), f.api.sent.single().tagNames)
    assertTrue(f.store.rows.value.isEmpty())
  }

  @Test
  fun `URL changed during preflight must be checked again before POST`() = runTest {
    val f = Fixture()
    assertTrue(f.queue.create(draft).isOk)
    f.api.online = true
    val release = CompletableDeferred<Unit>()
    f.api.onCheck = { release.await() }
    val retry = backgroundScope.launch { f.queue.retry() }
    runCurrent()
    val changed = draft.copy(url = "https://example.com/changed")
    assertTrue(f.queue.edit(1, changed))
    release.complete(Unit)
    retry.join()
    assertTrue(f.api.sent.isEmpty(), "The old URL check cannot authorize sending the new URL")
    assertEquals(changed, f.queue.observeFirst().bookmark)
    f.queue.retry()
    assertEquals(listOf(draft.url, changed.url), f.api.checkedUrls)
    assertEquals(changed.url, f.api.sent.single().url)
  }

  @Test
  fun `discard during preflight returns immediately and prevents POST`() = runTest {
    val f = Fixture()
    assertTrue(f.queue.create(draft).isOk)
    f.api.online = true
    val release = CompletableDeferred<Unit>()
    f.api.onCheck = { release.await() }
    val retry = backgroundScope.launch { f.queue.retry() }
    runCurrent()
    val discard = async { f.queue.discard(1) }
    runCurrent()
    assertTrue(discard.isCompleted, "Discard must not wait for a URL check")
    discard.await()
    release.complete(Unit)
    retry.join()
    assertTrue(f.store.rows.value.isEmpty())
    assertTrue(f.api.sent.isEmpty())
  }

  @Test
  fun `stale preflight failure cannot overwrite a newer local edit`() = runTest {
    val f = Fixture()
    assertTrue(f.queue.create(draft).isOk)
    f.api.checkError = LinkdingError.Other("Rejected old fields")
    val release = CompletableDeferred<Unit>()
    f.api.onCheck = { release.await() }
    val retry = backgroundScope.launch { f.queue.retry() }
    runCurrent()
    val changed = draft.copy(title = "Corrected")
    assertTrue(f.queue.edit(1, changed))
    release.complete(Unit)
    retry.join()
    assertEquals(changed, f.queue.observeFirst().bookmark)
    assertEquals(PendingBookmark.State.Pending, f.queue.observeFirst().state)
    assertEquals(null, f.queue.observeFirst().lastError)
  }

  @Test
  fun `a stalled POST cannot block local operations on another row`() = runTest {
    val f = Fixture()
    assertTrue(f.queue.create(draft).isOk)
    f.api.online = true
    f.api.onSave = { awaitCancellation() }
    backgroundScope.launch { f.queue.retry(1) }
    runCurrent()
    assertEquals(PendingBookmark.State.Uploading, f.queue.observeFirst().state)
    val save = async { f.queue.create(draft.copy(url = "https://example.com/second")) }
    runCurrent()
    assertTrue(save.isCompleted)
    assertTrue(save.await().isOk)
    val edit = async { f.queue.edit(2, draft.copy(title = "Second")) }
    runCurrent()
    assertTrue(edit.isCompleted)
    assertTrue(edit.await())
    val discard = async { f.queue.discard(2) }
    runCurrent()
    assertTrue(discard.isCompleted)
    discard.await()
    assertEquals(null, f.queue.get(2))
  }

  @Test
  fun `overlapping drains still send a pending creation only once`() = runTest {
    val f = Fixture()
    assertTrue(f.queue.create(draft).isOk)
    f.api.online = true
    val release = CompletableDeferred<Unit>()
    f.api.onSave = { release.await() }
    val first = backgroundScope.launch { f.queue.retry() }
    val second = backgroundScope.launch { f.queue.retry() }
    runCurrent()
    assertEquals(1, f.api.sent.size)
    release.complete(Unit)
    first.join()
    second.join()
    assertEquals(1, f.api.sent.size)
    assertTrue(f.store.rows.value.isEmpty())
  }

  private suspend fun DefaultPendingBookmarks.observeFirst() = get(1) ?: error("No pending item")

  private class Fixture {
    val store = MemoryPendingDao()
    val cache = MemoryBookmarksDao()
    val api = FakeApi()
    val preferences = FakePreferences()
    val online = MutableStateFlow(true)
    private val connectivity =
        object : ConnectivityObserver {
          override val observeIsOnline = online
        }

    fun newQueue(config: ApiConfig = initialConfig) =
        DefaultPendingBookmarks(
            store,
            cache,
            api,
            BookmarkMapper(),
            preferences,
            config,
            connectivity,
        )

    val queue = newQueue()
  }

  private class MemoryPendingDao : PendingBookmarksDao {
    val rows = MutableStateFlow<List<Pair<String, PendingBookmark>>>(emptyList())
    private var nextId = 1L

    override fun observeAll(accountId: String): Flow<List<PendingBookmark>> = rows.map { items ->
      items.filter { it.first == accountId }.map { it.second }
    }

    override suspend fun getAll(accountId: String) =
        rows.value.filter { it.first == accountId }.map { it.second }

    override suspend fun insert(accountId: String, bookmark: PendingBookmark): Long {
      val id = nextId++
      rows.value += accountId to bookmark.copy(id = id)
      return id
    }

    override suspend fun update(accountId: String, bookmark: PendingBookmark) {
      rows.value =
          rows.value.map {
            if (it.first == accountId && it.second.id == bookmark.id) accountId to bookmark else it
          }
    }

    override suspend fun delete(accountId: String, id: Long) {
      rows.value = rows.value.filterNot { it.first == accountId && it.second.id == id }
    }
  }

  private class MemoryBookmarksDao : BookmarksDao {
    val rows = mutableListOf<Bookmark>()

    override suspend fun insert(bookmark: Bookmark) {
      rows.add(bookmark)
    }

    override suspend fun insert(bookmarks: List<Bookmark>) {
      rows.addAll(bookmarks)
    }

    override suspend fun update(bookmark: Bookmark) {
      rows.removeAll { it.id == bookmark.id }
      rows.add(bookmark)
    }

    override suspend fun upsert(bookmark: Bookmark): Long {
      update(bookmark)
      return bookmark.id
    }

    override suspend fun upsert(bookmarks: List<Bookmark>) = bookmarks.map { upsert(it) }

    override suspend fun delete(id: Long) {
      rows.removeAll { it.id == id }
    }

    override suspend fun deleteAll() {
      rows.clear()
    }
  }

  private class FakePreferences : AppPreferences {
    var config: ApiConfig? = initialConfig
    var localAccountId = "account"

    override fun getApiConfig() = config

    override fun getAccountId() = localAccountId

    override fun observeApiConfig() = MutableStateFlow(config)

    override suspend fun setApiConfig(apiConfig: ApiConfig?) {
      config = apiConfig
    }

    override fun observeAppTheme() = MutableStateFlow(AppTheme.System)

    override suspend fun setAppTheme(appTheme: AppTheme) = Unit

    override fun observeUseDynamicColors() = MutableStateFlow(false)

    override suspend fun toggleUseDynamicColors() = Unit
  }

  private class FakeApi : LinkdingBookmarksApi {
    var online = false
    var checkError: LinkdingError? = null
    var saveError: LinkdingError? = null
    var existing: LinkdingBookmark? = null
    var onCheck: suspend () -> Unit = {}
    var onSave: suspend () -> Unit = {}
    val checkedUrls = mutableListOf<String>()
    val sent = mutableListOf<LinkdingSaveBookmarkRequest>()

    override suspend fun checkUrl(url: String): Result<LinkdingCheckUrlResponse, LinkdingError> {
      checkedUrls += url
      onCheck()
      checkError?.let {
        return Err(it)
      }
      if (!online) return Err(LinkdingError.Connectivity())
      return Ok(
          LinkdingCheckUrlResponse(existing, LinkdingCheckUrlResponse.Metadata(url, null, null))
      )
    }

    override suspend fun saveBookmark(
        request: LinkdingSaveBookmarkRequest
    ): Result<LinkdingBookmark, LinkdingError> {
      sent += request
      onSave()
      saveError?.let {
        return Err(it)
      }
      return Ok(
          remote.copy(
              title = request.title.orEmpty(),
              notes = request.notes.orEmpty(),
              tagNames = request.tagNames,
          )
      )
    }

    override suspend fun getBookmarks(
        offset: Int,
        limit: Int,
        query: String,
        category: LinkdingBookmarkCategory,
        tags: List<String>,
    ): Result<LinkdingBookmarksResponse, LinkdingError> = error("Unexpected read")

    override suspend fun getBookmark(id: Long): Result<LinkdingBookmark, LinkdingError> =
        error("Unexpected read")

    override suspend fun updateBookmark(
        id: Long,
        request: LinkdingUpdateBookmarkRequest,
    ): Result<LinkdingBookmark, LinkdingError> = error("Must not edit server bookmarks")

    override suspend fun archiveBookmark(id: Long): Result<Unit, LinkdingError> =
        error("Must not archive")

    override suspend fun unarchiveBookmark(id: Long): Result<Unit, LinkdingError> =
        error("Must not unarchive")

    override suspend fun deleteBookmark(id: Long): Result<Unit, LinkdingError> =
        error("Must not delete")
  }

  companion object {
    private val initialConfig = ApiConfig("https://linkding.example", "secret")
    private val draft = SaveBookmark("https://example.com/article", title = "Article")
    private val remote = LinkdingBookmark(id = 42, url = draft.url)
  }
}
