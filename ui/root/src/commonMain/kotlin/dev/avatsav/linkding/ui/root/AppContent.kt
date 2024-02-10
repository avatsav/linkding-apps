package dev.avatsav.linkding.ui.root

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.slack.circuit.backstack.SaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.retained.LocalRetainedStateRegistry
import com.slack.circuit.retained.continuityRetainedStateRegistry
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.PopResult
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuitx.gesturenavigation.GestureNavigationDecoration
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.ui.UrlScreen
import dev.avatsav.linkding.ui.theme.LinkdingTheme
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

// https://github.com/evant/kotlin-inject?tab=readme-ov-file#function-support--assisted-injection
typealias AppContent = @Composable (
    backStack: SaveableBackStack,
    navigator: Navigator,
    onOpenUrl: (String) -> Unit,
    modifier: Modifier,
) -> Unit

@Inject
@Composable
fun AppContent(
    @Assisted backStack: SaveableBackStack,
    @Assisted navigator: Navigator,
    @Assisted onOpenUrl: (String) -> Unit,
    circuit: Circuit,
    logger: Logger,
    @Assisted modifier: Modifier = Modifier,
) {
    val linkdingNavigator: Navigator = remember(navigator) {
        LinkdingNavigator(navigator, onOpenUrl, logger)
    }
    CompositionLocalProvider(
        LocalRetainedStateRegistry provides continuityRetainedStateRegistry(),
    ) {
        CircuitCompositionLocals(circuit) {
            LinkdingTheme {
                NavigableCircuitContent(
                    navigator = linkdingNavigator,
                    backStack = backStack,
                    decoration = remember(navigator) { GestureNavigationDecoration(onBackInvoked = navigator::pop) },
                    modifier = modifier.fillMaxSize(),
                )
            }
        }
    }
}

private class LinkdingNavigator(
    private val navigator: Navigator,
    private val onOpenUrl: (String) -> Unit,
    private val logger: Logger,
) : Navigator {
    override fun goTo(screen: Screen) {
        logger.d { "goTo. Screen: $screen. Current stack: ${peekBackStack()}" }
        when (screen) {
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
    ): List<Screen> {
        logger.d { "resetRoot: newRoot:$newRoot. Current stack: ${peekBackStack()}" }
        return navigator.resetRoot(newRoot, saveState, restoreState)
    }

    override fun peek(): Screen? {
        return navigator.peek()
    }

    override fun peekBackStack(): List<Screen> {
        return navigator.peekBackStack()
    }
}
