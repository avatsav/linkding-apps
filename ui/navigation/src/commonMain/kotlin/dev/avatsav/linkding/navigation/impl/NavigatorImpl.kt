package dev.avatsav.linkding.navigation.impl

import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import dev.avatsav.linkding.navigation.NavResult
import dev.avatsav.linkding.navigation.Navigator

internal class NavigatorImpl(val backStack: NavBackStack<NavKey>) : Navigator {

  override fun goTo(screen: NavKey): Boolean {
    backStack.add(screen)
    return true
  }

  override fun pop(result: NavResult?): NavKey? {
    // Only pop if we have more than one screen (don't pop the root)
    return if (backStack.size > 1) {
      backStack.removeLast()
      // Return the screen that's now on top after popping
      backStack.lastOrNull()
    } else {
      null
    }
  }

  override fun peek(): NavKey? {
    return backStack.lastOrNull()
  }

  override fun peekBackStack(): List<NavKey> {
    // Return a copy of the backstack containing only NavScreen items
    return backStack
  }

  override fun resetRoot(newRoot: NavKey): Boolean {
    backStack.clear()
    backStack.add(newRoot)
    return true
  }
}
