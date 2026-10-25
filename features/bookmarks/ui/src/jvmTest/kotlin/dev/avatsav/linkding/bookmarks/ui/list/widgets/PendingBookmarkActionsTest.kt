package dev.avatsav.linkding.bookmarks.ui.list.widgets

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import dev.avatsav.linkding.data.model.PendingBookmark
import dev.avatsav.linkding.data.model.SaveBookmark
import kotlin.test.assertEquals
import kotlin.time.Instant
import org.junit.Rule
import org.junit.Test

class PendingBookmarkActionsTest {
  @get:Rule val compose = createComposeRule()

  @Test
  fun `edit dismisses toolbar and edits selected pending item`() {
    val actions = mutableListOf<String>()
    setActions(onDismiss = { actions += "dismiss" }, onEdit = { actions += "edit:${it.id}" })
    compose.onNodeWithContentDescription("Edit bookmark").performClick()
    assertEquals(listOf("dismiss", "edit:1"), actions)
  }

  @Test
  fun `retry dismisses toolbar and retries selected pending item`() {
    val actions = mutableListOf<String>()
    setActions(onDismiss = { actions += "dismiss" }, onRetry = { actions += "retry:${it.id}" })
    compose.onNodeWithContentDescription("Retry").performClick()
    assertEquals(listOf("dismiss", "retry:1"), actions)
  }

  @Test
  fun `discard requires confirmation`() {
    val actions = mutableListOf<String>()
    setActions(onDismiss = { actions += "dismiss" }, onDiscard = { actions += "discard:${it.id}" })
    compose.onNodeWithContentDescription("Discard").performClick()
    compose.onNodeWithText("Discard pending bookmark?").assertExists()
    assertEquals(emptyList(), actions)
    compose.onNodeWithText("Discard").performClick()
    assertEquals(listOf("dismiss", "discard:1"), actions)
  }

  @Test
  fun `cancel keeps pending item and toolbar`() {
    val actions = mutableListOf<String>()
    setActions(onDismiss = { actions += "dismiss" }, onDiscard = { actions += "discard" })
    compose.onNodeWithContentDescription("Discard").performClick()
    compose.onNodeWithText("Cancel").performClick()
    compose.onNodeWithText("Discard pending bookmark?").assertDoesNotExist()
    compose.onNodeWithContentDescription("Edit bookmark").assertExists()
    assertEquals(emptyList(), actions)
  }

  @Test
  fun `uploading disables actions`() {
    setActions(item = { pendingBookmark().copy(state = PendingBookmark.State.Uploading) })
    compose.onNodeWithContentDescription("Edit bookmark").assertIsNotEnabled()
    compose.onNodeWithContentDescription("Retry").assertIsNotEnabled()
    compose.onNodeWithContentDescription("Discard").assertIsNotEnabled()
  }

  @Test
  fun `upload starting while confirmation is open disables discard`() {
    var pending by mutableStateOf(pendingBookmark())
    setActions(item = { pending })
    compose.onNodeWithContentDescription("Discard").performClick()
    compose.runOnIdle { pending = pending.copy(state = PendingBookmark.State.Uploading) }
    compose.onNodeWithText("Discard").assertIsNotEnabled()
  }

  @Test
  fun `changing selected item clears discard confirmation`() {
    var pending by mutableStateOf(pendingBookmark())
    setActions(item = { pending })
    compose.onNodeWithContentDescription("Discard").performClick()
    compose.runOnIdle { pending = pending.copy(id = 2) }
    compose.onNodeWithText("Discard pending bookmark?").assertDoesNotExist()
  }

  private fun setActions(
    item: () -> PendingBookmark = { pendingBookmark() },
    onDismiss: () -> Unit = {},
    onEdit: (PendingBookmark) -> Unit = {},
    onRetry: (PendingBookmark) -> Unit = {},
    onDiscard: (PendingBookmark) -> Unit = {},
  ) {
    compose.setContent {
      MaterialTheme {
        BookmarkActionsToolbar(visible = true) {
          PendingBookmarkActions(
            pending = item(),
            onDismiss = onDismiss,
            onEdit = onEdit,
            onRetry = onRetry,
            onDiscard = onDiscard,
          )
        }
      }
    }
  }

  private fun pendingBookmark() =
    PendingBookmark(
      id = 1,
      bookmark = SaveBookmark("https://example.com/pending", title = "Pending example"),
      createdAt = Instant.parse("2026-03-14T10:00:00Z"),
    )
}
