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

  private val routeA = TestRoutes.routeA
  private val routeB = TestRoutes.routeB
  private val routeC = TestRoutes.routeC

  @Test
  fun `goTo records event and updates backstack`() = runTest {
    val navigator = FakeNavigator(routeA)

    navigator.goTo(routeB)

    val event = navigator.awaitGoTo()
    event.route shouldBe routeB
    event.success.shouldBeTrue()

    navigator.currentRoute shouldBe routeB
    navigator.cancel()
  }

  @Test
  fun `goTo to same route records failure`() = runTest {
    val navigator = FakeNavigator(routeA)

    navigator.goTo(routeB)
    navigator.awaitGoTo() // consume first event

    navigator.goTo(routeB) // same route

    val event = navigator.awaitGoTo()
    event.success.shouldBeFalse()
    navigator.cancel()
  }

  @Test
  fun `awaitNextRoute returns navigated route`() = runTest {
    val navigator = FakeNavigator(routeA)

    navigator.goTo(routeB)

    navigator.awaitNextRoute() shouldBe routeB
    navigator.cancel()
  }

  @Test
  fun `pop records event`() = runTest {
    val navigator = FakeNavigator(routeA)

    navigator.goTo(routeB)
    navigator.awaitGoTo() // consume

    navigator.pop()

    val event = navigator.awaitPop()
    event.poppedRoute shouldBe routeB
    event.result.shouldBeNull()
    navigator.cancel()
  }

  @Test
  fun `pop with result records result`() = runTest {
    val navigator = FakeNavigator(routeA)
    val resultRoute = TestRoutes.resultRoute()

    navigator.goTo(resultRoute)
    navigator.awaitGoTo() // consume

    navigator.pop(TestRoutes.Results.dismissed)

    val event = navigator.awaitPop()
    event.poppedRoute shouldBe resultRoute
    event.result shouldBe TestRoutes.Results.dismissed
    navigator.cancel()
  }

  @Test
  fun `pop at root records null poppedRoute`() = runTest {
    val navigator = FakeNavigator(routeA)

    navigator.pop()

    val event = navigator.awaitPop()
    event.poppedRoute.shouldBeNull()
    navigator.cancel()
  }

  @Test
  fun `resetRoot records event with old routes`() = runTest {
    val navigator = FakeNavigator(routeA)

    navigator.goTo(routeB)
    navigator.awaitGoTo() // consume

    navigator.resetRoot(routeC)

    val event = navigator.awaitResetRoot()
    event.newRoot shouldBe routeC
    event.oldRoutes shouldContainExactly listOf(routeA, routeB)
    navigator.cancel()
  }

  @Test
  fun `peekBackStack returns current state`() = runTest {
    val navigator = FakeNavigator(routeA)

    navigator.goTo(routeB)
    navigator.goTo(routeC)

    navigator.peekBackStack() shouldContainExactly listOf(routeA, routeB, routeC)
    navigator.cancel()
  }

  @Test
  fun `peek returns current route`() = runTest {
    val navigator = FakeNavigator(routeA)

    navigator.goTo(routeB)

    navigator.peek() shouldBe routeB
    navigator.cancel()
  }

  @Test
  fun `initializing with multiple routes works`() = runTest {
    val nav = FakeNavigator(routeA, routeB, routeC)

    nav.currentRoute shouldBe routeC
    nav.peekBackStack() shouldContainExactly listOf(routeA, routeB, routeC)
    nav.cancel()
  }
}
