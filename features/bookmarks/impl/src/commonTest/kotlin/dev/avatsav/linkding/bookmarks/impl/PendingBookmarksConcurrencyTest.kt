package dev.avatsav.linkding.bookmarks.impl

import dev.avatsav.linkding.api.models.LinkdingError
import dev.avatsav.linkding.bookmarks.impl.PendingBookmarksFixture.Companion.draft
import dev.avatsav.linkding.bookmarks.impl.PendingBookmarksFixture.Companion.initialConfig
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.data.model.PendingBookmark
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

class PendingBookmarksConcurrencyTest {
  @Test
  fun `retry queued behind a skipped automatic lookup is not lost`() = runTest {
    val f = PendingBookmarksFixture()
    f.api.online = true
    assertTrue(f.queue.create(draft).isOk)
    f.store.update("account", f.queue.observeFirst().copy(state = PendingBookmark.State.Failed))
    val lookupStarted = CompletableDeferred<Unit>()
    val releaseLookup = CompletableDeferred<Unit>()
    var reads = 0
    f.store.onRead = {
      // Recovery and the drain snapshot precede the per-row lookup.
      if (++reads == 3) {
        lookupStarted.complete(Unit)
        releaseLookup.await()
      }
    }
    backgroundScope.launch { f.queue.runUploader() }
    runCurrent()
    assertTrue(lookupStarted.isCompleted)
    val request = async { f.newQueue().retry(1) }
    runCurrent()
    assertFalse(request.isCompleted)
    releaseLookup.complete(Unit)
    runCurrent()
    request.await()
    assertEquals(1, f.api.sent.size, "A newer Retry must survive an older skipped attempt")
    assertTrue(f.store.rows.value.isEmpty())
  }

  @Test
  fun `reset serializes an empty snapshot with another graph creation`() = runTest {
    val f = PendingBookmarksFixture()
    val snapshotRead = CompletableDeferred<Unit>()
    val releaseSnapshot = CompletableDeferred<Unit>()
    var reads = 0
    f.store.onRead = {
      if (++reads == 1) {
        snapshotRead.complete(Unit)
        releaseSnapshot.await()
      }
    }
    val reset = async { f.queue.resetAccountIfEmpty() }
    runCurrent()
    assertTrue(snapshotRead.isCompleted)
    val save = async { f.newQueue().create(draft) }
    runCurrent()
    releaseSnapshot.complete(Unit)
    runCurrent()
    assertTrue(reset.await())
    assertTrue(save.await().isErr, "A reset must not orphan an acknowledged concurrent creation")
    assertTrue(f.store.rows.value.isEmpty())
  }

  @Test
  fun `retry recorded after a visible POST failure survives attempt cleanup`() = runTest {
    val f = PendingBookmarksFixture()
    f.api.online = true
    f.api.saveError = LinkdingError.Other("Rejected")
    assertTrue(f.queue.create(draft).isOk)
    val failureVisible = CompletableDeferred<Unit>()
    val releaseFailure = CompletableDeferred<Unit>()
    f.store.onUpdate = { item ->
      if (item.state == PendingBookmark.State.Failed) {
        failureVisible.complete(Unit)
        releaseFailure.await()
      }
    }
    backgroundScope.launch { f.queue.runUploader() }
    runCurrent()
    assertTrue(failureVisible.isCompleted)
    val request = async { f.newQueue().retry(1) }
    runCurrent()
    f.api.saveError = null
    releaseFailure.complete(Unit)
    runCurrent()
    request.await()
    assertEquals(2, f.api.sent.size)
    assertTrue(f.store.rows.value.isEmpty())
  }

  @Test
  fun `an in-flight durable creation blocks account reset`() = runTest {
    val f = PendingBookmarksFixture()
    val insertStarted = CompletableDeferred<Unit>()
    val releaseInsert = CompletableDeferred<Unit>()
    f.store.onInsert = {
      insertStarted.complete(Unit)
      releaseInsert.await()
    }
    val save = async { f.queue.create(draft) }
    runCurrent()
    assertTrue(insertStarted.isCompleted)
    val reset = async { f.newQueue().resetAccountIfEmpty() }
    runCurrent()
    assertFalse(reset.isCompleted)
    releaseInsert.complete(Unit)
    runCurrent()
    assertTrue(save.await().isOk)
    assertFalse(reset.await())
    assertEquals(initialConfig, f.preferences.config)
    assertEquals(draft, f.newQueue().observeFirst().bookmark)
  }

  @Test
  fun `every unsent state blocks reset and preserves the account`() = runTest {
    for (state in PendingBookmark.State.entries) {
      val f = PendingBookmarksFixture()
      assertTrue(f.queue.create(draft).isOk)
      f.store.update("account", f.queue.observeFirst().copy(state = state))
      assertFalse(f.queue.resetAccountIfEmpty(), "$state must block reset")
      assertEquals(initialConfig, f.preferences.config)
      assertEquals("account", f.preferences.localAccountId)
    }
  }

  @Test
  fun `a stale queue cannot reset a replacement account`() = runTest {
    val f = PendingBookmarksFixture()
    f.preferences.localAccountId = "new-account"
    f.preferences.config = ApiConfig("https://other.example", "other-key")
    assertFalse(f.queue.resetAccountIfEmpty())
    assertEquals("new-account", f.preferences.localAccountId)
    assertEquals("https://other.example", f.preferences.config?.hostUrl)
  }

  @Test
  fun `reset rechecks the current account after the database read`() = runTest {
    val f = PendingBookmarksFixture()
    f.store.onRead = {
      f.preferences.localAccountId = "replacement-account"
      f.preferences.config = ApiConfig("https://other.example", "other-key")
    }
    assertFalse(f.queue.resetAccountIfEmpty())
    assertEquals("https://other.example", f.preferences.config?.hostUrl)
  }
}
