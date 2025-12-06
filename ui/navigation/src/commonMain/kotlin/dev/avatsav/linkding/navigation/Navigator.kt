package dev.avatsav.linkding.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import dev.avatsav.linkding.navigation.impl.NavigatorImpl

interface Navigator {
  /** The current screen on top of the backstack. */
  val currentScreen: Screen?

  fun goTo(screen: Screen): Boolean

  fun pop(result: NavResult? = null): Screen?

  fun peek(): Screen?

  fun peekBackStack(): List<Screen>

  fun resetRoot(newRoot: Screen): Boolean
}

class NoOpNavigator : Navigator {
  override val currentScreen: Screen? = null

  override fun goTo(screen: Screen): Boolean = false

  override fun pop(result: NavResult?): Screen? = null

  override fun peek(): Screen? = null

  override fun peekBackStack(): List<Screen> = emptyList()

  override fun resetRoot(newRoot: Screen): Boolean = false
}

@Composable
fun rememberNavigator(
  backStack: NavBackStack<NavKey>,
  resultHandler: NavigationResultHandler,
  onOpenUrl: ((String) -> Boolean)? = null,
): Navigator {
  return remember { NavigatorImpl(backStack, resultHandler, onOpenUrl) }
}
