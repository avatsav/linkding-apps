package dev.avatsav.linkding.navigation.impl

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import dev.avatsav.linkding.navigation.NavResult
import dev.avatsav.linkding.navigation.NavigationResultHandler
import dev.avatsav.linkding.navigation.Navigator
import dev.avatsav.linkding.navigation.Screen

internal class NavigatorImpl(
  private val backStack: NavBackStack<NavKey>,
  private val resultHandler: NavigationResultHandler,
  private val onOpenUrl: ((String) -> Boolean)? = null,
) : Navigator {

  override val currentScreen: Screen?
    get() = backStack.lastOrNull() as? Screen

  override fun goTo(screen: Screen): Boolean {
    if (screen is Screen.Url && onOpenUrl != null) {
      return onOpenUrl(screen.url)
    }
    backStack.add(screen)
    return true
  }

  override fun pop(result: NavResult?): Screen? {
    // Only pop if we have more than one screen (don't pop the root)
    if (backStack.size <= 1) return null

    // Get the screen being popped (current top)
    val poppedScreen = backStack.lastOrNull() as? Screen

    // Get the screen we're returning to (will be the new top after pop)
    val returningToScreen =
      if (backStack.size > 1) {
        backStack[backStack.size - 2] as? Screen
      } else {
        null
      }

    // Pop the screen
    backStack.removeLast()

    // If there's a result and a screen to return to, route the result
    if (result != null && poppedScreen != null && returningToScreen != null) {
      // The result should go to any result handler that was set up by the returning screen
      // We use the returning screen's key as the caller key
      // The resultKey is managed by rememberResultNavigator
      routeResult(returningToScreen.key, result)
    }

    return currentScreen
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

  override fun peek(): Screen? = currentScreen

  override fun peekBackStack(): List<Screen> {
    return backStack.mapNotNull { it as? Screen }
  }

  override fun resetRoot(newRoot: Screen): Boolean {
    backStack.clear()
    backStack.add(newRoot)
    return true
  }
}
