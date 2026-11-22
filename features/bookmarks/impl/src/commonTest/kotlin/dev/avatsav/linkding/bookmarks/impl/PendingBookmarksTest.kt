package dev.avatsav.linkding.bookmarks.impl

import com.github.michaelbull.result.Ok
import dev.avatsav.linkding.api.models.LinkdingError
import dev.avatsav.linkding.bookmarks.impl.PendingBookmarksFixture.Companion.draft
import dev.avatsav.linkding.bookmarks.impl.PendingBookmarksFixture.Companion.remote
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.data.model.PendingBookmark
import dev.avatsav.linkding.data.model.SaveResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

class PendingBookmarksTest {
  @Test
  fun `retry requests wait for a foreground uploader`() = runTest {
    val f = PendingBookmarksFixture()
    f.api.online = true
    assertTrue(f.queue.create(draft).isOk)
    val item = f.queue.observeFirst()
    f.store.update("account", item.copy(state = PendingBookmark.State.Failed))
    retry(f.queue, item.id)
    assertTrue(f.api.checkedUrls.isEmpty(), "Retry must not own network work")
    assertTrue(f.api.sent.isEmpty())
    backgroundScope.launch { f.queue.runUploader() }
    runCurrent()
    assertEquals(1, f.api.sent.size)
    assertTrue(f.store.rows.value.isEmpty())
  }

  @Test
  fun `two queue instances share one foreground uploader`() = runTest {
    val f = PendingBookmarksFixture()
    f.api.online = true
    assertTrue(f.queue.create(draft).isOk)
    val release = CompletableDeferred<Unit>()
    f.api.onCheck = { release.await() }
    f.api.onSave = { awaitCancellation() }
    backgroundScope.launch { f.queue.runUploader() }
    backgroundScope.launch { f.newQueue().runUploader() }
    runCurrent()
    release.complete(Unit)
    runCurrent()
    assertEquals(1, f.api.checkedUrls.size)
    assertEquals(1, f.api.sent.size)
    assertEquals(PendingBookmark.State.Uploading, f.queue.observeFirst().state)
  }

  @Test
  fun `another foreground instance cannot recover a live POST as interrupted`() = runTest {
    val f = PendingBookmarksFixture()
    f.api.online = true
    assertTrue(f.queue.create(draft).isOk)
    f.api.onSave = { awaitCancellation() }
    backgroundScope.launch { f.queue.runUploader() }
    runCurrent()
    assertEquals(PendingBookmark.State.Uploading, f.queue.observeFirst().state)
    backgroundScope.launch { f.newQueue().runUploader() }
    runCurrent()
    assertEquals(PendingBookmark.State.Uploading, f.queue.observeFirst().state)
    assertEquals(1, f.api.sent.size)
  }

  @Test
  fun `missing local network permission keeps creations pending without contacting the API`() =
    runTest {
      val f = foregroundFixture()
      f.access.permissionGranted.value = false
      f.api.online = true
      assertTrue(f.queue.create(draft).isOk)
      retry(f.queue)
      assertTrue(f.api.checkedUrls.isEmpty())
      assertTrue(f.api.sent.isEmpty())
      assertEquals(PendingBookmark.State.Pending, f.queue.observeFirst().state)
    }

  @Test
  fun `granting local network permission wakes the foreground uploader`() = runTest {
    val f = PendingBookmarksFixture()
    f.access.permissionGranted.value = false
    f.api.online = true
    assertTrue(f.queue.create(draft).isOk)
    backgroundScope.launch { f.queue.runUploader() }
    runCurrent()
    assertTrue(f.api.checkedUrls.isEmpty())
    f.access.permissionGranted.value = true
    runCurrent()
    assertEquals(1, f.api.sent.size)
    assertTrue(f.store.rows.value.isEmpty())
  }

  @Test
  fun `local writes never wait for the uploader permission lookup`() = runTest {
    val f = foregroundFixture()
    assertTrue(f.queue.create(draft).isOk)
    f.access.onLookup = { awaitCancellation() }
    retry(f.queue)
    runCurrent()
    val save = async { f.queue.create(draft.copy(url = "https://example.com/second")) }
    runCurrent()
    assertTrue(save.isCompleted)
    assertTrue(save.await().isOk)
  }

  @Test
  fun `save finishes locally without waiting for an unreachable backend`() = runTest {
    val f = PendingBookmarksFixture()
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
    val f = foregroundFixture()
    f.online.value = false
    assertTrue(f.queue.create(draft).isOk)
    f.api.onCheck = { awaitCancellation() }
    retry(f.queue)
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
    val fixture = foregroundFixture()
    assertTrue(fixture.queue.create(draft).isOk)
    retry(fixture.queue)
    assertEquals(draft, fixture.store.rows.value.single().second.bookmark)
    assertEquals(PendingBookmark.State.Pending, fixture.store.rows.value.single().second.state)
    assertTrue(fixture.api.sent.isEmpty())
  }

  @Test
  fun `editing updates the durable creation and retry sends latest fields`() = runTest {
    val f = PendingBookmarksFixture()
    assertTrue(f.queue.create(draft).isOk)
    val id = f.queue.observeFirst().id
    val changed = draft.copy(title = "Updated", notes = "Offline notes", tags = setOf("new"))
    assertTrue(f.queue.edit(id, changed))
    // Reconstruct the queue to model a new authenticated session after process restart.
    val restarted = f.newQueue()
    f.api.online = true
    backgroundScope.launch { restarted.runUploader() }
    retry(restarted)
    assertEquals("Updated", f.api.sent.single().title)
    assertEquals("Offline notes", f.api.sent.single().notes)
    assertEquals(setOf("new"), f.api.sent.single().tagNames)
    assertTrue(f.store.rows.value.isEmpty())
    assertEquals("Updated", f.cache.rows.single().title)
  }

  @Test
  fun `discard never calls the server`() = runTest {
    val f = foregroundFixture()
    assertTrue(f.queue.create(draft).isOk)
    f.queue.discard(f.queue.observeFirst().id)
    f.api.online = true
    retry(f.queue)
    assertTrue(f.store.rows.value.isEmpty())
    assertTrue(f.api.sent.isEmpty())
  }

  @Test
  fun `authentication and validation rejections remain actionable on the pending item`() = runTest {
    for (error in listOf(LinkdingError.Unauthorized(), LinkdingError.Other("Invalid URL"))) {
      val f = foregroundFixture()
      f.api.checkError = error
      assertTrue(f.queue.create(draft).isOk)
      retry(f.queue)
      val pending = f.queue.observeFirst()
      assertEquals(draft, pending.bookmark)
      assertEquals(PendingBookmark.State.Failed, pending.state)
      assertEquals(error.message, pending.lastError)
      assertTrue(f.api.sent.isEmpty())
    }
  }

  @Test
  fun `POST rejection keeps the saved fields with an actionable failure`() = runTest {
    val f = foregroundFixture()
    f.api.online = true
    f.api.saveError = LinkdingError.Other("Invalid fields")
    assertTrue(f.queue.create(draft).isOk)
    retry(f.queue)
    assertEquals(draft, f.queue.observeFirst().bookmark)
    assertEquals(PendingBookmark.State.Failed, f.queue.observeFirst().state)
    assertEquals("Invalid fields", f.queue.observeFirst().lastError)
  }

  @Test
  fun `active and archived URL matches keep the draft and normal retry cannot overwrite`() =
    runTest {
      for (archived in listOf(false, true)) {
        val f = foregroundFixture()
        f.api.online = true
        f.api.existing = remote.copy(isArchived = archived)
        assertTrue(f.queue.create(draft).isOk)
        runCurrent()
        assertEquals(PendingBookmark.State.AlreadyExists, f.queue.observeFirst().state)
        assertEquals(draft, f.queue.observeFirst().bookmark)
        retry(f.queue, 1)
        retry(f.queue)
        assertEquals(listOf(draft.url), f.api.checkedUrls)
        assertTrue(f.api.sent.isEmpty())
        assertTrue(f.api.updates.isEmpty())
        assertTrue(f.cache.rows.isEmpty())
      }
    }

  @Test
  fun `discarding a conflict only removes the draft`() = runTest {
    val f = foregroundFixture()
    f.api.online = true
    f.api.existing = remote
    assertTrue(f.queue.create(draft).isOk)
    runCurrent()
    assertEquals(PendingBookmark.State.AlreadyExists, f.queue.observeFirst().state)
    f.queue.discard(1)
    runCurrent()
    assertTrue(f.store.rows.value.isEmpty())
    assertTrue(f.api.sent.isEmpty())
    assertTrue(f.cache.rows.isEmpty())
  }

  @Test
  fun `retry marks a conflict when an ambiguous POST now exists on the server`() = runTest {
    val f = foregroundFixture()
    f.api.online = true
    f.api.saveError = LinkdingError.Connectivity("Timed out")
    assertTrue(f.queue.create(draft).isOk)
    retry(f.queue)
    assertEquals(PendingBookmark.State.Uncertain, f.queue.observeFirst().state)
    f.api.existing = remote
    retry(f.queue)
    assertEquals(1, f.api.sent.size)
    assertEquals(PendingBookmark.State.AlreadyExists, f.queue.observeFirst().state)
    assertTrue(f.cache.rows.isEmpty())
  }

  @Test
  fun `invalid success response also preserves an uncertain creation`() = runTest {
    val f = foregroundFixture()
    f.api.online = true
    f.api.saveError = LinkdingError.Other("Invalid response", requestMayHaveSucceeded = true)
    assertTrue(f.queue.create(draft).isOk)
    retry(f.queue)
    assertEquals(PendingBookmark.State.Uncertain, f.queue.observeFirst().state)
  }

  @Test
  fun `uploader cancellation propagates and leaves a durable in-flight record`() = runTest {
    val f = PendingBookmarksFixture()
    f.api.online = true
    f.api.onSave = { awaitCancellation() }
    assertTrue(f.queue.create(draft).isOk)
    val uploader = backgroundScope.launch { f.queue.runUploader() }
    retry(f.queue)
    assertEquals(PendingBookmark.State.Uploading, f.queue.observeFirst().state)
    uploader.cancelAndJoin()
    assertTrue(uploader.isCancelled)
    assertEquals(PendingBookmark.State.Uncertain, f.queue.observeFirst().state)
  }

  @Test
  fun `foreground startup uploads pending items without an internet hint`() = runTest {
    val f = PendingBookmarksFixture()
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
    val f = PendingBookmarksFixture()
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
    val f = PendingBookmarksFixture()
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
    val f = foregroundFixture()
    assertTrue(f.queue.create(draft).isOk)
    val id = f.queue.observeFirst().id
    f.api.online = true
    val started = CompletableDeferred<Unit>()
    val release = CompletableDeferred<Unit>()
    f.api.onSave = {
      started.complete(Unit)
      release.await()
    }
    retry(f.queue)
    started.await()
    val edit = async { f.queue.edit(id, draft.copy(title = "Too late")) }
    runCurrent()
    assertTrue(edit.isCompleted, "An active POST must reject the edit without a network wait")
    assertFalse(edit.await())
    release.complete(Unit)
    runCurrent()
    assertTrue(f.store.rows.value.isEmpty())
  }

  @Test
  fun `old session cannot upload after credentials change and new account cannot see drafts`() =
    runTest {
      val f = foregroundFixture()
      assertTrue(f.queue.create(draft).isOk)
      f.preferences.localAccountId = "other-account"
      f.preferences.config = ApiConfig("https://other.example", "other-key")
      f.api.online = true
      retry(f.queue)
      assertTrue(f.api.sent.isEmpty())
      assertEquals(1, f.store.rows.value.size)
      val otherQueue = f.newQueue(f.preferences.config!!)
      assertEquals(null, otherQueue.get(f.store.rows.value.single().second.id))
      backgroundScope.launch { otherQueue.runUploader() }
      retry(otherQueue)
      assertTrue(f.api.sent.isEmpty())
    }

  @Test
  fun `edit during preflight returns immediately and POST uses the latest fields`() = runTest {
    val f = foregroundFixture()
    assertTrue(f.queue.create(draft).isOk)
    f.api.online = true
    val release = CompletableDeferred<Unit>()
    f.api.onCheck = { release.await() }
    retry(f.queue)
    runCurrent()
    val changed = draft.copy(title = "Latest", notes = "New notes", tags = setOf("edited"))
    val edit = async { f.queue.edit(1, changed) }
    runCurrent()
    assertTrue(edit.isCompleted, "An in-flight URL check must not block a local edit")
    assertTrue(edit.await())
    assertEquals(changed, f.queue.observeFirst().bookmark)
    release.complete(Unit)
    runCurrent()
    assertEquals("Latest", f.api.sent.single().title)
    assertEquals("New notes", f.api.sent.single().notes)
    assertEquals(setOf("edited"), f.api.sent.single().tagNames)
    assertTrue(f.store.rows.value.isEmpty())
  }

  @Test
  fun `URL changed during preflight must be checked again before POST`() = runTest {
    val f = foregroundFixture()
    assertTrue(f.queue.create(draft).isOk)
    f.api.online = true
    val release = CompletableDeferred<Unit>()
    f.api.onCheck = { release.await() }
    retry(f.queue)
    runCurrent()
    val changed = draft.copy(url = "https://example.com/changed")
    assertTrue(f.queue.edit(1, changed))
    val secondCheck = CompletableDeferred<Unit>()
    f.api.onCheck = { secondCheck.await() }
    release.complete(Unit)
    runCurrent()
    assertTrue(f.api.sent.isEmpty(), "The old URL check cannot authorize sending the new URL")
    assertEquals(changed, f.queue.observeFirst().bookmark)
    assertEquals(listOf(draft.url, changed.url), f.api.checkedUrls)
    secondCheck.complete(Unit)
    runCurrent()
    assertEquals(changed.url, f.api.sent.single().url)
  }

  @Test
  fun `discard during preflight returns immediately and prevents POST`() = runTest {
    val f = foregroundFixture()
    assertTrue(f.queue.create(draft).isOk)
    f.api.online = true
    val release = CompletableDeferred<Unit>()
    f.api.onCheck = { release.await() }
    retry(f.queue)
    runCurrent()
    val discard = async { f.queue.discard(1) }
    runCurrent()
    assertTrue(discard.isCompleted, "Discard must not wait for a URL check")
    discard.await()
    release.complete(Unit)
    runCurrent()
    assertTrue(f.store.rows.value.isEmpty())
    assertTrue(f.api.sent.isEmpty())
  }

  @Test
  fun `an existing bookmark for the old URL cannot clear an edited draft`() = runTest {
    val f = foregroundFixture()
    f.api.online = true
    f.api.existing = remote
    val release = CompletableDeferred<Unit>()
    f.api.onCheck = { release.await() }
    assertTrue(f.queue.create(draft).isOk)
    runCurrent()

    val changed = draft.copy(url = "https://example.com/changed")
    assertTrue(f.queue.edit(1, changed))
    // Hold the new check so only the old result can affect these assertions.
    f.api.onCheck = { awaitCancellation() }
    release.complete(Unit)
    runCurrent()

    assertEquals(listOf(draft.url, changed.url), f.api.checkedUrls)
    assertEquals(changed, f.queue.observeFirst().bookmark)
    assertTrue(f.cache.rows.isEmpty())
    assertTrue(f.api.sent.isEmpty())
  }

  @Test
  fun `stale preflight failure cannot overwrite a newer local edit`() = runTest {
    val f = foregroundFixture()
    assertTrue(f.queue.create(draft).isOk)
    f.api.checkError = LinkdingError.Other("Rejected old fields")
    val release = CompletableDeferred<Unit>()
    f.api.onCheck = { release.await() }
    retry(f.queue)
    runCurrent()
    val changed = draft.copy(title = "Corrected")
    assertTrue(f.queue.edit(1, changed))
    // Hold the new automatic preflight so only the stale response can affect the assertion.
    f.api.onCheck = { awaitCancellation() }
    release.complete(Unit)
    runCurrent()
    assertEquals(changed, f.queue.observeFirst().bookmark)
    assertEquals(PendingBookmark.State.Pending, f.queue.observeFirst().state)
    assertEquals(null, f.queue.observeFirst().lastError)
  }

  @Test
  fun `a stalled POST cannot block local operations on another row`() = runTest {
    val f = foregroundFixture()
    assertTrue(f.queue.create(draft).isOk)
    f.api.online = true
    f.api.onSave = { awaitCancellation() }
    retry(f.queue, 1)
    runCurrent()
    assertEquals(PendingBookmark.State.Uploading, f.queue.observeFirst().state)
    val peer = f.newQueue()
    val save = async { peer.create(draft.copy(url = "https://example.com/second")) }
    runCurrent()
    assertTrue(save.isCompleted)
    assertTrue(save.await().isOk)
    val edit = async { peer.edit(2, draft.copy(title = "Second")) }
    runCurrent()
    assertTrue(edit.isCompleted)
    assertTrue(edit.await())
    val discard = async { peer.discard(2) }
    runCurrent()
    assertTrue(discard.isCompleted)
    discard.await()
    assertEquals(null, f.queue.get(2))
  }

  @Test
  fun `overlapping drains still send a pending creation only once`() = runTest {
    val f = foregroundFixture()
    assertTrue(f.queue.create(draft).isOk)
    f.api.online = true
    val release = CompletableDeferred<Unit>()
    f.api.onSave = { release.await() }
    f.queue.retry()
    f.queue.retry()
    runCurrent()
    assertEquals(1, f.api.sent.size)
    release.complete(Unit)
    runCurrent()
    assertEquals(1, f.api.sent.size)
    assertTrue(f.store.rows.value.isEmpty())
  }

  @Test
  fun `backgrounding during manual preflight stops the batch until foreground resumes`() = runTest {
    val f = PendingBookmarksFixture()
    f.api.online = true
    assertTrue(f.queue.create(draft).isOk)
    assertTrue(f.queue.create(draft.copy(url = "https://example.com/second")).isOk)
    for (item in f.store.getAll("account")) {
      f.store.update("account", item.copy(state = PendingBookmark.State.Failed))
    }
    val release = CompletableDeferred<Unit>()
    f.api.onCheck = { release.await() }
    val uploader = backgroundScope.launch { f.queue.runUploader() }
    retry(f.queue)
    assertEquals(listOf(draft.url), f.api.checkedUrls)
    uploader.cancelAndJoin()
    release.complete(Unit)
    runCurrent()
    assertTrue(f.api.sent.isEmpty())
    assertEquals(1, f.api.checkedUrls.size, "No later preflight may start in the background")
    backgroundScope.launch { f.newQueue().runUploader() }
    runCurrent()
    assertEquals(2, f.api.sent.size)
    assertTrue(f.store.rows.value.isEmpty())
  }

  @Test
  fun `foreground handoff never automatically retries an ambiguous POST`() = runTest {
    val f = PendingBookmarksFixture()
    f.api.online = true
    assertTrue(f.queue.create(draft).isOk)
    f.api.onSave = { awaitCancellation() }
    val first = backgroundScope.launch { f.queue.runUploader() }
    runCurrent()
    val peer = f.newQueue()
    backgroundScope.launch { peer.runUploader() }
    // Refresh during a POST must not authorize a retry of its still-unknown outcome.
    retry(peer)
    first.cancelAndJoin()
    runCurrent()
    assertEquals(1, f.api.sent.size)
    assertEquals(PendingBookmark.State.Uncertain, peer.observeFirst().state)
    f.api.onSave = {}
    retry(peer, 1)
    assertEquals(2, f.api.sent.size)
    assertTrue(f.store.rows.value.isEmpty())
  }

  @Test
  fun `retry from another graph reaches the active foreground owner`() = runTest {
    val f = PendingBookmarksFixture()
    f.api.online = true
    assertTrue(f.queue.create(draft).isOk)
    val item = f.queue.observeFirst()
    f.store.update("account", item.copy(state = PendingBookmark.State.Uncertain))
    backgroundScope.launch { f.queue.runUploader() }
    runCurrent()
    assertTrue(f.api.sent.isEmpty())
    retry(f.newQueue(), item.id)
    assertEquals(1, f.api.sent.size)
    assertTrue(f.store.rows.value.isEmpty())
  }

  @Test
  fun `local creation in another graph wakes the active foreground owner`() = runTest {
    val f = PendingBookmarksFixture()
    f.api.online = true
    backgroundScope.launch { f.queue.runUploader() }
    runCurrent()
    assertEquals(Ok(SaveResult(pendingId = 1)), f.newQueue().create(draft))
    assertTrue(f.api.sent.isEmpty(), "Create must still return before network work")
    runCurrent()
    assertEquals(1, f.api.sent.size)
    assertTrue(f.store.rows.value.isEmpty())
  }

  @Test
  fun `explicit retry survives missing local network permission`() = runTest {
    val f = foregroundFixture()
    f.api.online = true
    f.access.permissionGranted.value = false
    assertTrue(f.queue.create(draft).isOk)
    val item = f.queue.observeFirst()
    f.store.update("account", item.copy(state = PendingBookmark.State.Failed))
    retry(f.queue, item.id)
    assertTrue(f.api.checkedUrls.isEmpty())
    f.access.permissionGranted.value = true
    runCurrent()
    assertEquals(1, f.api.sent.size)
    assertTrue(f.store.rows.value.isEmpty())
  }

  @Test
  fun `new account uploader does not wait for the old account foreground owner`() = runTest {
    val f = foregroundFixture()
    assertTrue(f.queue.create(draft).isOk)
    runCurrent()
    f.preferences.localAccountId = "other-account"
    f.preferences.config = ApiConfig("https://other.example", "other-key")
    val other = f.newQueue(f.preferences.config!!)
    val otherDraft = draft.copy(url = "https://example.com/other")
    assertTrue(other.create(otherDraft).isOk)
    f.api.online = true
    backgroundScope.launch { other.runUploader() }
    runCurrent()
    assertEquals(otherDraft.url, f.api.sent.single().url)
    assertEquals(draft, f.store.getAll("account").single().bookmark)
    assertTrue(f.store.getAll("other-account").isEmpty())
  }

  private fun TestScope.foregroundFixture(): PendingBookmarksFixture =
    PendingBookmarksFixture().also { fixture ->
      backgroundScope.launch { fixture.queue.runUploader() }
    }

  private suspend fun TestScope.retry(queue: DefaultPendingBookmarks, id: Long? = null) {
    queue.retry(id)
    runCurrent()
  }
}
