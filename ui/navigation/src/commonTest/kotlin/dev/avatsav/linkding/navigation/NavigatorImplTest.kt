package dev.avatsav.linkding.navigation

import dev.avatsav.linkding.navigation.impl.NavigatorImpl
import io.kotest.matchers.booleans.shouldBeTrue
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.shouldBe
import kotlin.test.Test

class NavigatorImplTest {

  private val routeA = TestRoutes.routeA
  private val routeB = TestRoutes.routeB
  private val routeC = TestRoutes.routeC

  private val backStack = RouteBackStack(routeA)
  private val resultHandler = NavigationResultHandler()
  private val navigator = NavigatorImpl(backStack, resultHandler)

  @Test
  fun `currentRoute returns top of backstack`() {
    navigator.currentRoute shouldBe routeA
  }

  @Test
  fun `currentRoute returns null for empty backstack`() {
    val emptyBackStack = RouteBackStack()
    val nav = NavigatorImpl(emptyBackStack, resultHandler)
    nav.currentRoute.shouldBeNull()
  }

  @Test
  fun `goTo adds route to backstack`() {
    navigator.goTo(routeB)

    navigator.currentRoute shouldBe routeB
    navigator.peekBackStack() shouldHaveSize 2
    navigator.peekBackStack() shouldContainExactly listOf(routeA, routeB)
  }

  @Test
  fun `goTo returns true on success`() {
    navigator.goTo(routeB).shouldBeTrue()
  }

  @Test
  fun `goTo with Url route calls onOpenUrl handler`() {
    var openedUrl: String? = null
    val nav =
      NavigatorImpl(
        backStack,
        resultHandler,
        onOpenUrl = { url ->
          openedUrl = url
          true
        },
      )

    nav.goTo(Route.Url("https://example.com"))

    openedUrl shouldBe "https://example.com"
  }

  @Test
  fun `goTo with Url route does not add to backstack when handler present`() {
    val nav = NavigatorImpl(backStack, resultHandler, onOpenUrl = { true })
    val initialSize = nav.peekBackStack().size

    nav.goTo(Route.Url("https://example.com"))

    nav.peekBackStack() shouldHaveSize initialSize
  }

  @Test
  fun `goTo with Url route adds to backstack when no handler`() {
    // No onOpenUrl handler
    val urlRoute = Route.Url("https://example.com")
    navigator.goTo(urlRoute)

    navigator.currentRoute shouldBe urlRoute
  }

  @Test
  fun `pop removes top route`() {
    navigator.goTo(routeB)
    navigator.goTo(routeC)

    navigator.pop()

    navigator.currentRoute shouldBe routeB
    navigator.peekBackStack() shouldHaveSize 2
  }

  @Test
  fun `pop returns new current route`() {
    navigator.goTo(routeB)

    val result = navigator.pop()

    result shouldBe routeA
  }

  @Test
  fun `pop does not pop root route`() {
    // Only one route in backstack
    val result = navigator.pop()

    result.shouldBeNull()
    navigator.currentRoute shouldBe routeA
    navigator.peekBackStack() shouldHaveSize 1
  }

  @Test
  fun `pop at root invokes onRootPop callback`() {
    var onRootPopCount = 0
    val nav = NavigatorImpl(backStack, resultHandler, onRootPop = { onRootPopCount++ })

    // Only one route in backstack, pop should invoke callback
    nav.pop()

    onRootPopCount shouldBe 1
  }

  @Test
  fun `pop not at root does not invoke onRootPop callback`() {
    var onRootPopCount = 0
    val nav = NavigatorImpl(backStack, resultHandler, onRootPop = { onRootPopCount++ })

    nav.goTo(routeB)
    nav.pop()

    onRootPopCount shouldBe 0
  }

  @Test
  fun `pop with result routes to result handler`() {
    val callerRoute = TestRoutes.routeA
    val callerBackStack = RouteBackStack(callerRoute)
    val handler = NavigationResultHandler()
    val nav = NavigatorImpl(callerBackStack, handler)

    // Push target route
    nav.goTo(TestRoutes.resultRoute())

    // Prepare the caller to receive result
    handler.prepareForResult(callerRoute.key, NavigationResultHandler.RESULT_KEY_FROM_POP)

    // Pop with result
    nav.pop(TestRoutes.Results.dismissed)

    // Verify result was routed
    handler.awaitResult(callerRoute.key, NavigationResultHandler.RESULT_KEY_FROM_POP) shouldBe
      TestRoutes.Results.dismissed
  }

  @Test
  fun `pop with result does nothing when no caller awaiting`() {
    navigator.goTo(TestRoutes.resultRoute())

    // Pop with result but no one prepared to receive
    navigator.pop(TestRoutes.Results.dismissed)

    // Verify no result stored (since no one was awaiting)
    resultHandler
      .awaitResult(routeA.key, NavigationResultHandler.RESULT_KEY_FROM_POP)
      .shouldBeNull()
  }

  @Test
  fun `peek returns current route without modifying backstack`() {
    navigator.goTo(routeB)

    navigator.peek() shouldBe routeB
    navigator.peekBackStack() shouldHaveSize 2
  }

  @Test
  fun `peekBackStack returns copy of backstack`() {
    navigator.goTo(routeB)
    navigator.goTo(routeC)

    val peeked = navigator.peekBackStack()

    peeked shouldContainExactly listOf(routeA, routeB, routeC)
  }

  @Test
  fun `resetRoot clears backstack and sets new root`() {
    navigator.goTo(routeB)
    navigator.goTo(routeC)

    val newRoot = TestRoutes.routeWithParam(42)
    navigator.resetRoot(newRoot)

    navigator.peekBackStack() shouldHaveSize 1
    navigator.currentRoute shouldBe newRoot
  }

  @Test
  fun `resetRoot returns true`() {
    navigator.resetRoot(routeB).shouldBeTrue()
  }

  @Test
  fun `multiple navigations work correctly`() {
    val paramRoute = TestRoutes.routeWithParam(1)
    navigator.goTo(routeB)
    navigator.goTo(routeC)
    navigator.pop()
    navigator.goTo(paramRoute)

    navigator.peekBackStack() shouldContainExactly listOf(routeA, routeB, paramRoute)
  }
}
