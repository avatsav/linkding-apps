package dev.avatsav.linkding.ui

import co.touchlab.kermit.Logger
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.PopResult
import com.slack.circuit.runtime.screen.Screen
import kotlinx.collections.immutable.ImmutableList

internal class AppNavigator(
  private val navigator: Navigator,
  private val onOpenUrl: (String) -> Boolean,
) : Navigator {
  override fun goTo(screen: Screen): Boolean {
    Logger.d { "goTo. Screen: $screen. Current stack: ${peekBackStack()}" }
    return when (screen) {
      is UrlScreen -> onOpenUrl(screen.url)
      else -> navigator.goTo(screen)
    }
  }

  override fun pop(result: PopResult?): Screen? {
    Logger.d { "pop. Current stack: ${peekBackStack()} " }
    return navigator.pop(result)
  }

  override fun resetRoot(
    newRoot: Screen,
    saveState: Boolean,
    restoreState: Boolean,
  ): ImmutableList<Screen> {
    Logger.d { "resetRoot: newRoot:$newRoot. Current stack: ${peekBackStack()}" }
    return navigator.resetRoot(newRoot, saveState, restoreState)
  }

  override fun peek(): Screen? = navigator.peek()

  override fun peekBackStack(): ImmutableList<Screen> = navigator.peekBackStack()
}
