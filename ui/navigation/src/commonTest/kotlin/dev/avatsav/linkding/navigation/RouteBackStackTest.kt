package dev.avatsav.linkding.navigation

import io.kotest.matchers.booleans.shouldBeFalse
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldBeEmpty
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class RouteBackStackTest {

  private val routeA = TestRoutes.routeA
  private val routeB = TestRoutes.routeB
  private val routeC = TestRoutes.routeC

  @Test
  fun `empty constructor creates empty backstack`() {
    val backStack = RouteBackStack()
    backStack.shouldBeEmpty()
  }

  @Test
  fun `vararg constructor initializes with routes`() {
    val backStack = RouteBackStack(routeA, routeB, routeC)

    backStack shouldHaveSize 3
    backStack shouldContainExactly listOf(routeA, routeB, routeC)
  }

  @Test
  fun `add appends route to backstack`() {
    val backStack = RouteBackStack(routeA)

    backStack.add(routeB)

    backStack shouldHaveSize 2
    backStack.last() shouldBe routeB
  }

  @Test
  fun `plusAssign appends route to backstack`() {
    val backStack = RouteBackStack(routeA)

    backStack += routeB

    backStack shouldHaveSize 2
    backStack.last() shouldBe routeB
  }

  @Test
  fun `removeLast removes top route`() {
    val backStack = RouteBackStack(routeA, routeB)

    val removed = backStack.removeLast()

    removed shouldBe routeB
    backStack shouldHaveSize 1
    backStack.last() shouldBe routeA
  }

  @Test
  fun `clear removes all routes`() {
    val backStack = RouteBackStack(routeA, routeB, routeC)

    backStack.clear()

    backStack.shouldBeEmpty()
  }

  @Test
  fun `lastOrNull returns last route`() {
    val backStack = RouteBackStack(routeA, routeB)

    backStack.lastOrNull() shouldBe routeB
  }

  @Test
  fun `lastOrNull returns null for empty backstack`() {
    val backStack = RouteBackStack()

    backStack.lastOrNull() shouldBe null
  }

  @Test
  fun `size returns number of routes`() {
    val backStack = RouteBackStack(routeA, routeB)

    backStack.size shouldBe 2
  }

  @Test
  fun `isEmpty returns true for empty backstack`() {
    val backStack = RouteBackStack()

    backStack.isEmpty().shouldBeTrue()
  }

  @Test
  fun `isEmpty returns false for non-empty backstack`() {
    val backStack = RouteBackStack(routeA)

    backStack.isEmpty().shouldBeFalse()
  }

  @Test
  fun `toList returns copy of routes`() {
    val backStack = RouteBackStack(routeA, routeB)

    val list = backStack.toList()

    list shouldContainExactly listOf(routeA, routeB)
  }

  @Test
  fun `index access works correctly`() {
    val backStack = RouteBackStack(routeA, routeB, routeC)

    backStack[0] shouldBe routeA
    backStack[1] shouldBe routeB
    backStack[2] shouldBe routeC
  }

  @Test
  fun `contains returns true for existing route`() {
    val backStack = RouteBackStack(routeA)

    backStack.contains(routeA).shouldBeTrue()
  }

  @Test
  fun `contains returns false for non-existing route`() {
    val backStack = RouteBackStack(routeA)

    backStack.contains(routeB).shouldBeFalse()
  }
}
