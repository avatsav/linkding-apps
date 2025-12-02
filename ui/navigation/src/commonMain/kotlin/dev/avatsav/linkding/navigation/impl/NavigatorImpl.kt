package dev.avatsav.linkding.navigation.impl

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import dev.avatsav.linkding.navigation.NavResult
import dev.avatsav.linkding.navigation.Navigator
import dev.avatsav.linkding.navigation.Screen

internal class NavigatorImpl(
  private val backStack: NavBackStack<NavKey>,
  private val onOpenUrl: ((String) -> Boolean)? = null,
) : Navigator {

  override fun goTo(screen: Screen): Boolean {
    if (screen is Screen.Url && onOpenUrl != null) {
      return onOpenUrl(screen.url)
    }
    backStack.add(screen)
    return true
  }

  override fun pop(result: NavResult?): Screen? {
    // Only pop if we have more than one screen (don't pop the root)
    return if (backStack.size > 1) {
      backStack.removeLast()
      // Return the screen that's now on top after popping
      backStack.lastOrNull() as Screen
    } else {
      null
    }
  }

  override fun peek(): Screen? {
    return backStack.lastOrNull() as Screen?
  }

  override fun peekBackStack(): List<Screen> {
    // Return a copy of the backstack containing only NavScreen items
    return backStack.map { it as Screen }
  }

  override fun resetRoot(newRoot: Screen): Boolean {
    backStack.clear()
    backStack.add(newRoot)
    return true
  }
}
