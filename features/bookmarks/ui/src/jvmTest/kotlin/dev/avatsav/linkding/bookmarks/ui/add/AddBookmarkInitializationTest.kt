package dev.avatsav.linkding.bookmarks.ui.add

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import dev.avatsav.linkding.data.model.Bookmark
import dev.avatsav.linkding.data.model.PendingBookmark
import dev.avatsav.linkding.data.model.SaveBookmark
import kotlin.test.assertEquals
import kotlin.time.Instant
import org.junit.Rule
import org.junit.Test

class AddBookmarkInitializationTest {
  @get:Rule val compose = createComposeRule()

  @Test
  fun `pending draft initializes every form field`() {
    val events = mutableListOf<AddBookmarkUiEvent>()
    setContent({ pendingState() }, events)
    compose.onNodeWithText("Save").performClick()
    assertEquals(listOf<AddBookmarkUiEvent>(expectedSave), events)
  }

  @Test
  fun `server bookmark initializes the same form fields`() {
    val events = mutableListOf<AddBookmarkUiEvent>()
    val state =
      AddBookmarkUiState(
        mode = BookmarkMode.Edit(77),
        existingBookmark =
          Bookmark(
            id = 77,
            url = draft.url,
            urlHost = "example.com",
            title = "Article",
            description = "Description",
            notes = "Notes",
            tags = setOf("reading"),
          ),
      )
    setContent({ state }, events)
    compose.onNodeWithText("Save").performClick()
    assertEquals(listOf<AddBookmarkUiEvent>(expectedSave), events)
  }

  @Test
  fun `a later draft emission does not overwrite user edits`() {
    val events = mutableListOf<AddBookmarkUiEvent>()
    var state by mutableStateOf(pendingState())
    setContent({ state }, events)
    compose.onNodeWithText("Title").performTextReplacement("Local title")
    compose.runOnIdle {
      state =
        state.copy(
          pendingBookmark =
            state.pendingBookmark!!.copy(bookmark = draft.copy(title = "Later database title"))
        )
    }
    compose.onNodeWithText("Save").performClick()
    assertEquals(listOf<AddBookmarkUiEvent>(expectedSave.copy(title = "Local title")), events)
  }

  @Test
  fun `a conflict disables save without resetting the typed title`() {
    val events = mutableListOf<AddBookmarkUiEvent>()
    var state by mutableStateOf(pendingState())
    setContent({ state }, events)
    compose.onNodeWithText("Title").performTextReplacement("Local title")
    compose.runOnIdle {
      state =
        state.copy(
          pendingBookmark = state.pendingBookmark?.copy(state = PendingBookmark.State.AlreadyExists)
        )
    }
    compose.onNodeWithText("Bookmark already exists").assertExists()
    compose.onNodeWithText("Save").assertIsNotEnabled()
    compose.onNodeWithText("Local title").assertExists()
    assertEquals(emptyList(), events)
  }

  private fun setContent(
    state: () -> AddBookmarkUiState,
    events: MutableList<AddBookmarkUiEvent>,
  ) {
    compose.setContent {
      MaterialTheme {
        AddBookmark(state(), remember { SnackbarHostState() }, eventSink = { events += it })
      }
    }
  }

  private fun pendingState() =
    AddBookmarkUiState(
      mode = BookmarkMode.Pending(1),
      pendingBookmark =
        PendingBookmark(
          id = 1,
          bookmark = draft,
          createdAt = Instant.parse("2026-01-01T00:00:00Z"),
        ),
    )

  companion object {
    private val draft =
      SaveBookmark(
        url = "https://example.com/article",
        title = "Article",
        description = "Description",
        notes = "Notes",
        tags = setOf("reading"),
      )
    private val expectedSave =
      AddBookmarkUiEvent.Save(
        url = "https://example.com/article",
        title = "Article",
        description = "Description",
        notes = "Notes",
        tags = listOf("reading"),
      )
  }
}
