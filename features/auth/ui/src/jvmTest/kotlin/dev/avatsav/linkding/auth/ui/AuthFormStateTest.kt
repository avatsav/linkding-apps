package dev.avatsav.linkding.auth.ui

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.LocalSaveableStateRegistry
import androidx.compose.runtime.saveable.SaveableStateRegistry
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.junit4.v2.createComposeRule
import kotlin.test.assertEquals
import kotlin.test.assertNotSame
import kotlin.test.assertNull
import org.junit.Rule
import org.junit.Test

class AuthFormStateTest {
  @get:Rule val compose = createComposeRule()

  @Test
  fun `grant resumes Save once after form recreation`() {
    val form = RestorableForm()
    form.setContent()
    compose.runOnIdle {
      form.current.hostUrl = "https://linkding.local"
      form.current.apiKey = "test-key"
      form.current.awaitPermissionResult()
    }
    val previous = form.current
    form.recreate()
    compose.runOnIdle {
      assertNotSame(previous, form.current)
      assertEquals("https://linkding.local", form.current.hostUrl)
      assertEquals("test-key", form.current.apiKey)
      assertEquals(
        AuthUiEvent.SaveCredentials("https://linkding.local", "test-key"),
        form.current.consumePermissionResult(true),
      )
      assertNull(
        form.current.consumePermissionResult(true),
        "The result must not replay Save twice",
      )
    }
    form.recreate()
    compose.runOnIdle { assertNull(form.current.consumePermissionResult(true)) }
  }

  @Test
  fun `denial clears Save intent but preserves credentials for another attempt`() {
    val form = RestorableForm()
    form.setContent()
    compose.runOnIdle {
      form.current.hostUrl = "https://linkding.local"
      form.current.apiKey = "test-key"
      form.current.awaitPermissionResult()
    }
    form.recreate()
    compose.runOnIdle {
      assertNull(form.current.consumePermissionResult(false))
      assertNull(form.current.consumePermissionResult(true))
      assertEquals("https://linkding.local", form.current.hostUrl)
      assertEquals("test-key", form.current.apiKey)
      form.current.awaitPermissionResult()
      assertEquals(
        AuthUiEvent.SaveCredentials("https://linkding.local", "test-key"),
        form.current.consumePermissionResult(true),
      )
    }
  }

  @Test
  fun `unsolicited grant does not submit credentials`() {
    val form = RestorableForm()
    form.setContent()
    compose.runOnIdle {
      form.current.hostUrl = "https://linkding.local"
      form.current.apiKey = "test-key"
    }
    form.recreate()
    compose.runOnIdle { assertNull(form.current.consumePermissionResult(true)) }
  }

  private inner class RestorableForm {
    lateinit var current: AuthFormState
    private var registry by mutableStateOf(SaveableStateRegistry(null) { true })
    private var visible by mutableStateOf(true)

    fun setContent() {
      compose.setContent {
        CompositionLocalProvider(LocalSaveableStateRegistry provides registry) {
          if (visible) current = rememberAuthFormState()
        }
      }
    }

    fun recreate() {
      // Desktop's StateRestorationTester has an unimplemented platform encoder. Exercise the
      // real rememberSaveable registry through disposal/restoration without that encoder.
      val saved = compose.runOnIdle { registry.performSave().also { visible = false } }
      compose.runOnIdle {
        registry = SaveableStateRegistry(saved) { true }
        visible = true
      }
      compose.waitForIdle()
    }
  }
}
