package dev.avatsav.linkding.auth.ui

import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import kotlin.test.assertEquals
import org.junit.Rule
import org.junit.Test

class AuthScreenTest {
  @get:Rule val compose = createComposeRule()

  @Test
  fun `Save and permission recovery submit the fields owned by the restorable form`() {
    val form = AuthFormState()
    val events = mutableListOf<AuthUiEvent>()
    compose.setContent {
      MaterialTheme {
        AuthScreen(
          state = AuthUiState(localNetworkAccessRequired = true),
          form = form,
          opensSettings = true,
          eventSink = { events += it },
        )
      }
    }
    compose.onNodeWithText("Host URL").performTextInput("https://linkding.local")
    compose.onNodeWithText("API Key").performTextInput("test-key")
    compose.onNodeWithContentDescription("Show API key").performClick()
    compose.onNodeWithContentDescription("Hide API key").performClick()
    compose.onNodeWithText("Open app settings").performClick()
    compose.onNodeWithText("Save").performClick()
    val credentials = AuthUiEvent.SaveCredentials("https://linkding.local", "test-key")
    assertEquals(listOf<AuthUiEvent>(credentials, credentials), events)
  }
}
