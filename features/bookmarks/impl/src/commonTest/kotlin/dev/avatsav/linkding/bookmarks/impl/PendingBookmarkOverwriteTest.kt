package dev.avatsav.linkding.bookmarks.impl

import dev.avatsav.linkding.api.models.LinkdingUpdateBookmarkRequest
import dev.avatsav.linkding.bookmarks.impl.PendingBookmarksFixture.Companion.draft
import dev.avatsav.linkding.bookmarks.impl.PendingBookmarksFixture.Companion.remote
import dev.avatsav.linkding.bookmarks.impl.mappers.BookmarkMapper
import dev.avatsav.linkding.data.model.PendingBookmark
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest

class PendingBookmarkOverwriteTest {
  @Test
  fun `confirmed overwrite patches draft fields without touching flags or adding a cache row`() =
    runTest {
      val f = foregroundFixture()
      val edited = draft.copy(notes = "Local notes", tags = setOf("saved-offline"))
      f.api.online = true
      f.api.existing = remote.copy(isArchived = true, unread = true, shared = true)
      assertTrue(f.queue.create(edited).isOk)
      runCurrent()

      assertEquals(PendingBookmark.State.AlreadyExists, f.queue.observeFirst().state)
      f.newQueue().overwrite(1)
      assertTrue(f.api.updates.isEmpty(), "Confirming must leave network work to the uploader")
      runCurrent()

      assertEquals(listOf(edited.url, edited.url), f.api.checkedUrls)
      assertTrue(f.api.sent.isEmpty(), "Overwrite must not use the create endpoint")
      assertEquals(
        remote.id to LinkdingUpdateBookmarkRequest("Article", "", "Local notes", edited.tags),
        f.api.updates.single(),
      )
      val updated = checkNotNull(f.api.existing)
      assertTrue(updated.isArchived)
      assertTrue(updated.unread)
      assertTrue(updated.shared)
      assertTrue(f.store.rows.value.isEmpty())
      assertTrue(f.cache.rows.isEmpty())
    }

  @Test
  fun `overwrite refreshes an already cached bookmark`() = runTest {
    val f = foregroundFixture()
    val existing = remote.copy(title = "Old title", unread = true)
    f.api.online = true
    f.api.existing = existing
    f.cache.insert(BookmarkMapper().map(existing))
    val edited = draft.copy(title = "New title", tags = setOf("updated"))
    assertTrue(f.queue.create(edited).isOk)
    runCurrent()

    f.queue.overwrite(1)
    runCurrent()

    val cached = f.cache.rows.single()
    assertEquals(existing.id, cached.id)
    assertEquals(edited.title, cached.title)
    assertEquals(edited.tags, cached.tags)
    assertTrue(cached.unread)
    assertTrue(f.store.rows.value.isEmpty())
  }

  @Test
  fun `overwrite does not create a new bookmark if the match disappears`() = runTest {
    val f = foregroundFixture()
    f.api.online = true
    f.api.existing = remote
    assertTrue(f.queue.create(draft).isOk)
    runCurrent()
    assertEquals(PendingBookmark.State.AlreadyExists, f.queue.observeFirst().state)

    f.api.existing = null
    f.queue.overwrite(1)
    runCurrent()

    assertTrue(f.api.sent.isEmpty())
    assertTrue(f.api.updates.isEmpty())
    assertEquals(PendingBookmark.State.Failed, f.queue.observeFirst().state)
    assertEquals(draft, f.queue.observeFirst().bookmark)

    f.queue.retry(1)
    runCurrent()
    assertEquals(draft.url, f.api.sent.single().url)
    assertTrue(f.store.rows.value.isEmpty())
  }

  private fun TestScope.foregroundFixture(): PendingBookmarksFixture =
    PendingBookmarksFixture().also { fixture ->
      backgroundScope.launch { fixture.queue.runUploader() }
    }
}
