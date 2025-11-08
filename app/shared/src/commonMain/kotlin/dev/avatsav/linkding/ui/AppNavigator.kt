package dev.avatsav.linkding.ui

import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.PopResult
import com.slack.circuit.runtime.screen.Screen

internal class AppNavigator(
  private val navigator: Navigator,
  private val onOpenUrl: (String) -> Boolean,
) : Navigator {
  override fun goTo(screen: Screen): Boolean {
    return when (screen) {
      is UrlScreen -> onOpenUrl(screen.url)
      else -> navigator.goTo(screen)
    }
  }

  override fun pop(result: PopResult?): Screen? {
    return navigator.pop(result)
  }

  override fun resetRoot(newRoot: Screen, options: Navigator.StateOptions): List<Screen> {
    return navigator.resetRoot(newRoot, options)
  }

  override fun peek(): Screen? = navigator.peek()

  override fun peekBackStack(): List<Screen> = navigator.peekBackStack()
}
