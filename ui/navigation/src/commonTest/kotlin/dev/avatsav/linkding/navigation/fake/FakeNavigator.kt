package dev.avatsav.linkding.navigation.fake

import app.cash.turbine.Turbine
import dev.avatsav.linkding.navigation.NavResult
import dev.avatsav.linkding.navigation.Navigator
import dev.avatsav.linkding.navigation.Route
import dev.avatsav.linkding.navigation.RouteBackStack

/**
 * A fake [Navigator] implementation for testing that records all navigation events.
 *
 * Uses Turbine internally to provide async-safe assertions for navigation calls.
 *
 * ## Usage
 *
 * ```kotlin
 * @Test
 * fun testNavigation() = runTest {
 *   val navigator = FakeNavigator(TestRoute.Home)
 *
 *   // Perform navigation
 *   navigator.goTo(TestRoute.Details)
 *
 *   // Assert
 *   navigator.awaitGoTo() shouldBe TestRoute.Details
 *   navigator.assertGoToIsEmpty()
 * }
 * ```
 */
class FakeNavigator(vararg initialRoutes: Route) : Navigator {

  private val backStack = RouteBackStack(*initialRoutes)

  private val goToEvents = Turbine<GoToEvent>()
  private val popEvents = Turbine<PopEvent>()
  private val resetRootEvents = Turbine<ResetRootEvent>()

  override val currentRoute: Route?
    get() = backStack.lastOrNull()

  override fun goTo(route: Route): Boolean {
    val currentTop = backStack.lastOrNull()
    // Don't navigate to the same route
    if (currentTop == route) {
      goToEvents.add(GoToEvent(route, success = false))
      return false
    }
    backStack.add(route)
    goToEvents.add(GoToEvent(route, success = true))
    return true
  }

  override fun pop(result: NavResult?): Route? {
    if (backStack.size <= 1) {
      popEvents.add(PopEvent(poppedRoute = null, result = result))
      return null
    }
    val poppedRoute = backStack.removeLastOrNull()
    popEvents.add(PopEvent(poppedRoute = poppedRoute, result = result))
    return backStack.lastOrNull()
  }

  override fun peek(): Route? = backStack.lastOrNull()

  override fun peekBackStack(): List<Route> = backStack.toList()

  override fun resetRoot(newRoot: Route): Boolean {
    val oldRoutes = backStack.toList()
    backStack.clear()
    backStack.add(newRoot)
    resetRootEvents.add(ResetRootEvent(newRoot = newRoot, oldRoutes = oldRoutes))
    return true
  }

  // ===== Assertion methods =====

  /** Await and return the next goTo event. */
  suspend fun awaitGoTo(): GoToEvent = goToEvents.awaitItem()

  /** Await and return the next route navigated to. */
  suspend fun awaitNextRoute(): Route = goToEvents.awaitItem().route

  /** Await and return the next pop event. */
  suspend fun awaitPop(): PopEvent = popEvents.awaitItem()

  /** Await and return the next resetRoot event. */
  suspend fun awaitResetRoot(): ResetRootEvent = resetRootEvents.awaitItem()

  /** Assert that no goTo events are pending. */
  fun assertGoToIsEmpty() {
    goToEvents.ensureAllEventsConsumed()
  }

  /** Assert that no pop events are pending. */
  fun assertPopIsEmpty() {
    popEvents.ensureAllEventsConsumed()
  }

  /** Assert that no resetRoot events are pending. */
  fun assertResetRootIsEmpty() {
    resetRootEvents.ensureAllEventsConsumed()
  }

  /** Cancel all pending events. Call this in test cleanup. */
  suspend fun cancel() {
    goToEvents.cancel()
    popEvents.cancel()
    resetRootEvents.cancel()
  }

  /** Event recorded when [goTo] is called. */
  data class GoToEvent(val route: Route, val success: Boolean)

  /** Event recorded when [pop] is called. */
  data class PopEvent(val poppedRoute: Route?, val result: NavResult? = null)

  /** Event recorded when [resetRoot] is called. */
  data class ResetRootEvent(val newRoot: Route, val oldRoutes: List<Route>)
}
