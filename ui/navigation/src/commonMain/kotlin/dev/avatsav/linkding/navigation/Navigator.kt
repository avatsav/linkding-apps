package dev.avatsav.linkding.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.savedstate.serialization.SavedStateConfiguration
import dev.avatsav.linkding.navigation.impl.NavigatorImpl

@Stable
interface Navigator {
  fun goTo(screen: NavKey): Boolean

  fun pop(result: NavResult? = null): NavKey?

  fun peek(): NavKey?

  fun peekBackStack(): List<NavKey>

  fun resetRoot(newRoot: NavKey): Boolean
}

@Composable
fun rememberNavigator(savedStateConfiguration: SavedStateConfiguration, start: NavKey): Navigator {
  val backStack = rememberNavBackStack(savedStateConfiguration, start)
  return remember(start) { NavigatorImpl(backStack) }
}
