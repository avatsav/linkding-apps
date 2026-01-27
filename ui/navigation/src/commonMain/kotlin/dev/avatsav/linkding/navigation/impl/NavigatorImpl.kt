package dev.avatsav.linkding.navigation.impl

import co.touchlab.kermit.Logger
import dev.avatsav.linkding.navigation.NavResult
import dev.avatsav.linkding.navigation.NavigationResultHandler
import dev.avatsav.linkding.navigation.Navigator
import dev.avatsav.linkding.navigation.Route
import dev.avatsav.linkding.navigation.RouteBackStack

internal class NavigatorImpl(
  private val backStack: RouteBackStack,
  internal val resultHandler: NavigationResultHandler,
  private val onOpenUrl: ((String) -> Boolean)? = null,
  private val onRootPop: () -> Unit = {},
) : Navigator {

  override val currentRoute: Route?
    get() = backStack.lastOrNull()

  override fun goTo(route: Route): Boolean {
    if (route is Route.Url && onOpenUrl != null) {
      Logger.d { "Opening URL: ${route.url}" }
      return onOpenUrl(route.url)
    }
    Logger.d { "goTo: ${route.key}" }
    backStack.add(route)
    return true
  }

  override fun pop(result: NavResult?): Route? {
    // When at root, invoke callback and return null (don't pop the root)
    if (backStack.size <= 1) {
      Logger.d { "pop: root" }
      onRootPop()
      return null
    }

    // Get the route being popped (current top)
    val poppedRoute = backStack.lastOrNull()

    // Get the route we're returning to (will be the new top after pop)
    val returningToRoute = if (backStack.size > 1) backStack[backStack.size - 2] else null

    // Pop the route
    backStack.removeLast()

    // If there's a result and a route to return to, route the result
    if (result != null && poppedRoute != null && returningToRoute != null) {
      routeResult(returningToRoute.key, result)
    }
    Logger.d { "pop: $currentRoute" }
    return currentRoute
  }

  /**
   * Route a result to any handler expecting it. Since we don't know the specific resultKey here, we
   * broadcast to all handlers waiting for results from this caller.
   */
  private fun routeResult(callerKey: String, result: NavResult) {
    // The AnsweringResultHandler uses (callerKey, resultKey) pairs
    // We need to find any pending result expectation for this caller
    // For simplicity, we use a wildcard approach where pop() sends to a known key
    resultHandler.sendResult(callerKey, NavigationResultHandler.RESULT_KEY_FROM_POP, result)
  }

  override fun peek(): Route? = currentRoute

  override fun peekBackStack(): List<Route> = backStack.toList()

  override fun resetRoot(newRoot: Route): Boolean {
    backStack.clear()
    backStack.add(newRoot)
    return true
  }
}
