package dev.avatsav.linkding.bookmarks.ui.list.widgets

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.PendingBookmark
import dev.avatsav.linkding.data.model.SaveBookmark
import kotlin.test.assertEquals
import kotlin.time.Instant
import org.junit.Rule
import org.junit.Test

class PendingBookmarkInteractionsTest {
  @get:Rule val compose = createComposeRule()

  @Test
  fun `tap opens pending bookmark instead of editing`() {
    val actions = mutableListOf<String>()
    setRow(onOpen = { actions += it.bookmark.url }, onActions = { actions += "actions" })
    compose.onNodeWithText("Pending example").performClick()
    assertEquals(listOf("https://example.com/pending"), actions)
  }

  @Test
  fun `long press selects pending actions without opening bookmark`() {
    val actions = mutableListOf<String>()
    setRow(onOpen = { actions += "open" }, onActions = { actions += "actions:${it.id}" })
    compose.onNodeWithText("Pending example").performTouchInput { longClick() }
    assertEquals(listOf("actions:1"), actions)
    compose.onNodeWithContentDescription("Pending bookmark actions").assertDoesNotExist()
  }

  @Test
  fun `uploading bookmark can still be opened`() {
    var opened: String? = null
    setRow(
      pendingBookmark().copy(state = PendingBookmark.State.Uploading),
      onOpen = { opened = it.bookmark.url },
    )
    compose.onNodeWithText("Pending example").performClick()
    assertEquals("https://example.com/pending", opened)
  }

  @Test
  fun `regular bookmark keeps tap and long press behavior with shared row`() {
    val actions = mutableListOf<String>()
    compose.setContent {
      MaterialTheme {
        BookmarkListItem(
          bookmark =
            Bookmark(
              id = 7,
              url = "https://example.com/saved",
              urlHost = "example.com",
              title = "Saved example",
              description = "Saved description",
            ),
          selected = false,
          openBookmark = { actions += it.url },
          toggleActions = { actions += "actions:${it.id}" },
        )
      }
    }
    compose.onNodeWithText("Saved example").performClick()
    compose.onNodeWithText("Saved example").performTouchInput { longClick() }
    assertEquals(listOf("https://example.com/saved", "actions:7"), actions)
  }

  private fun setRow(
    pending: PendingBookmark = pendingBookmark(),
    onOpen: (PendingBookmark) -> Unit = {},
    onActions: (PendingBookmark) -> Unit = {},
  ) {
    compose.setContent {
      MaterialTheme {
        PendingBookmarkListItem(
          pending = pending,
          selected = false,
          openBookmark = onOpen,
          toggleActions = onActions,
        )
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
