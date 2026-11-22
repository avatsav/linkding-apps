package dev.avatsav.linkding.auth.ui

import com.github.michaelbull.result.Ok
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.auth.api.AuthRepository
import dev.avatsav.linkding.auth.ui.usecase.Authenticate
import dev.avatsav.linkding.internet.LocalNetworkAccess
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

class AuthPresenterTest {
  private val dispatcher = StandardTestDispatcher()
  private val requests = mutableListOf<AuthUiEvent.SaveCredentials>()
  private val presenters = mutableListOf<AuthPresenter>()
  private val access = FakeLocalNetworkAccess()

  @Before
  fun setUp() {
    Dispatchers.setMain(dispatcher)
  }

  @After
  fun tearDown() {
    presenters.forEach { it.close() }
    dispatcher.scheduler.runCurrent()
    Dispatchers.resetMain()
  }

  @Test
  fun `permission result resumes Save through a recreated presenter`() =
    runTest(dispatcher) {
      val original = presenter()
      runCurrent()
      original.eventSink(credentials)
      runCurrent()
      assertTrue(requests.isEmpty())
      assertEquals(
        AuthEffect.RequestLocalNetworkAccess,
        original.effects.tryReceive().getOrNull(),
      )
      original.close()

      val recreated = presenter()
      runCurrent()
      access.granted.value = true
      recreated.eventSink(AuthUiEvent.LocalNetworkPermissionResult(true, credentials))
      runCurrent()
      assertEquals(listOf(credentials), requests)
      assertEquals(AuthEffect.AuthenticationSuccess, recreated.effects.tryReceive().getOrNull())
    }

  @Test
  fun `denied or unsolicited permission results do not authenticate`() =
    runTest(dispatcher) {
      val presenter = presenter()
      runCurrent()
      presenter.eventSink(AuthUiEvent.LocalNetworkPermissionResult(false, credentials))
      runCurrent()
      assertTrue(presenter.models.value.localNetworkAccessRequired)
      access.granted.value = true
      presenter.eventSink(AuthUiEvent.LocalNetworkPermissionResult(true, null))
      runCurrent()
      assertTrue(requests.isEmpty())
      assertFalse(presenter.models.value.localNetworkAccessRequired)
    }

  private fun presenter(): AuthPresenter {
    val repository =
      object : AuthRepository {
        override suspend fun authenticate(hostUrl: String, apiKey: String) =
          Ok(Unit).also { requests += AuthUiEvent.SaveCredentials(hostUrl, apiKey) }
      }
    return AuthPresenter(
        Authenticate(repository, AppCoroutineDispatchers(dispatcher, dispatcher, dispatcher)),
        access,
      )
      .also {
        presenters += it
        assertFalse(it.models.value.loading)
      }
  }

  private class FakeLocalNetworkAccess : LocalNetworkAccess() {
    val granted = MutableStateFlow(false)
    override val permissionRequired = true
    override val permissionGranted = granted

    override suspend fun isLocalServer(hostUrl: String) = true

    override fun refresh() = Unit
  }

  companion object {
    private val credentials = AuthUiEvent.SaveCredentials("https://linkding.local", "test-key")
  }
}
