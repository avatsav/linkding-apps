package dev.avatsav.linkding.internet

import app.cash.turbine.test
import dev.avatsav.linkding.internet.fake.FakeNetworkMonitor
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest

@OptIn(ExperimentalCoroutinesApi::class)
class ConnectivityObserverTest {

  private val appCoroutineScope = TestScope(UnconfinedTestDispatcher())
  private val networkMonitor = FakeNetworkMonitor()
  private val victim = DefaultConnectivityObserver(networkMonitor, appCoroutineScope)

  @Test
  fun `network changes emit new states distinctly`() = runTest {
    victim.observeIsOnline.test {
      awaitItem() shouldBe true // initial value
      networkMonitor.setConnectivityChanged(false)
      networkMonitor.setConnectivityChanged(false)
      networkMonitor.setConnectivityChanged(false)
      networkMonitor.setConnectivityChanged(true)
      awaitItem() shouldBe false
      awaitItem() shouldBe true
    }
    appCoroutineScope.cancel()
    networkMonitor.closed shouldBe true
  }
}
