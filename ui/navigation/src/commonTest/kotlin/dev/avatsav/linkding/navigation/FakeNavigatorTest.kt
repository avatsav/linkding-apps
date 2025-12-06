package dev.avatsav.linkding.navigation

import dev.avatsav.linkding.navigation.fake.FakeNavigator
import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test
import kotlinx.coroutines.test.runTest

class FakeNavigatorTest {

  private val screenA = TestScreens.screenA()
  private val screenB = TestScreens.screenB()
  private val screenC = TestScreens.screenC()

  @Test
  fun `goTo records event and updates backstack`() = runTest {
    val navigator = FakeNavigator(screenA)

    navigator.goTo(screenB)

    val event = navigator.awaitGoTo()
    event.screen shouldBe screenB
    event.success.shouldBeTrue()

    navigator.currentScreen shouldBe screenB
    navigator.cancel()
  }

  @Test
  fun `goTo to same screen records failure`() = runTest {
    val navigator = FakeNavigator(screenA)

    navigator.goTo(screenB)
    navigator.awaitGoTo() // consume first event

    navigator.goTo(screenB) // same screen

    val event = navigator.awaitGoTo()
    event.success.shouldBeFalse()
    navigator.cancel()
  }

  @Test
  fun `awaitNextScreen returns navigated screen`() = runTest {
    val navigator = FakeNavigator(screenA)

    navigator.goTo(screenB)

    navigator.awaitNextScreen() shouldBe screenB
    navigator.cancel()
  }

  @Test
  fun `pop records event`() = runTest {
    val navigator = FakeNavigator(screenA)

    navigator.goTo(screenB)
    navigator.awaitGoTo() // consume

    navigator.pop()

    val event = navigator.awaitPop()
    event.poppedScreen shouldBe screenB
    event.result.shouldBeNull()
    navigator.cancel()
  }

  @Test
  fun `pop with result records result`() = runTest {
    val navigator = FakeNavigator(screenA)
    val resultScreen = TestScreens.resultScreen()

    navigator.goTo(resultScreen)
    navigator.awaitGoTo() // consume

    navigator.pop(TestScreens.Results.dismissed)

    val event = navigator.awaitPop()
    event.poppedScreen shouldBe resultScreen
    event.result shouldBe TestScreens.Results.dismissed
    navigator.cancel()
  }

  @Test
  fun `pop at root records null poppedScreen`() = runTest {
    val navigator = FakeNavigator(screenA)

    navigator.pop()

    val event = navigator.awaitPop()
    event.poppedScreen.shouldBeNull()
    navigator.cancel()
  }

  @Test
  fun `resetRoot records event with old screens`() = runTest {
    val navigator = FakeNavigator(screenA)

    navigator.goTo(screenB)
    navigator.awaitGoTo() // consume

    navigator.resetRoot(screenC)

    val event = navigator.awaitResetRoot()
    event.newRoot shouldBe screenC
    event.oldScreens shouldContainExactly listOf(screenA, screenB)
    navigator.cancel()
  }

  @Test
  fun `peekBackStack returns current state`() = runTest {
    val navigator = FakeNavigator(screenA)

    navigator.goTo(screenB)
    navigator.goTo(screenC)

    navigator.peekBackStack() shouldContainExactly listOf(screenA, screenB, screenC)
    navigator.cancel()
  }

  @Test
  fun `peek returns current screen`() = runTest {
    val navigator = FakeNavigator(screenA)

    navigator.goTo(screenB)

    navigator.peek() shouldBe screenB
    navigator.cancel()
  }

  @Test
  fun `initializing with multiple screens works`() = runTest {
    val nav = FakeNavigator(screenA, screenB, screenC)

    nav.currentScreen shouldBe screenC
    nav.peekBackStack() shouldContainExactly listOf(screenA, screenB, screenC)
    nav.cancel()
  }
}
