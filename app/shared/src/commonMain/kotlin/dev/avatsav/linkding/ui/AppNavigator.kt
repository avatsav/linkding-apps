package dev.avatsav.linkding.ui

import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.PopResult
import com.slack.circuit.runtime.screen.Screen
import dev.avatsav.linkding.Logger
import kotlinx.collections.immutable.ImmutableList

internal class AppNavigator(
    private val navigator: Navigator,
    private val onOpenUrl: (String) -> Boolean,
    private val logger: Logger,
) : Navigator {
    override fun goTo(screen: Screen): Boolean {
        logger.d { "goTo. Screen: $screen. Current stack: ${peekBackStack()}" }
        return when (screen) {
            is UrlScreen -> onOpenUrl(screen.url)
            else -> navigator.goTo(screen)
        }
    }

    override fun pop(result: PopResult?): Screen? {
        logger.d { "pop. Current stack: ${peekBackStack()} " }
        return navigator.pop(result)
    }

    override fun resetRoot(
        newRoot: Screen,
        saveState: Boolean,
        restoreState: Boolean,
    ): ImmutableList<Screen> {
        logger.d { "resetRoot: newRoot:$newRoot. Current stack: ${peekBackStack()}" }
        return navigator.resetRoot(newRoot, saveState, restoreState)
    }

    override fun peek(): Screen? = navigator.peek()

    override fun peekBackStack(): ImmutableList<Screen> = navigator.peekBackStack()
}
