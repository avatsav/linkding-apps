package dev.avatsav.linkding.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.slack.circuit.backstack.SaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.retained.LocalRetainedStateRegistry
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.retained.continuityRetainedStateRegistry
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.Screen
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.ui.UrlScreen
import dev.avatsav.linkding.ui.theme.LinkdingTheme
import kotlinx.coroutines.CoroutineScope
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

// https://github.com/evant/kotlin-inject?tab=readme-ov-file#function-support--assisted-injection
typealias AppContent = @Composable (
    backstack: SaveableBackStack,
    navigator: Navigator,
    onOpenUrl: (String) -> Unit,
    modifier: Modifier,
) -> Unit

@Inject
@Composable
fun AppContent(
    @Assisted backstack: SaveableBackStack,
    @Assisted navigator: Navigator,
    @Assisted onOpenUrl: (String) -> Unit,
    rootCoordinator: (CoroutineScope) -> RootCoordinator,
    circuit: Circuit,
    logger: Logger,
    @Assisted modifier: Modifier = Modifier,
) {
    val coroutineScope = rememberCoroutineScope()
    val linkdingNavigator: Navigator = remember(navigator) {
        LinkdingNavigator(navigator, backstack, onOpenUrl, logger)
    }
    val coordinator = remember { rootCoordinator(coroutineScope) }
    val apiConfiguration by coordinator.apiConfiguration.collectAsRetainedState()

    CompositionLocalProvider(
        LocalRetainedStateRegistry provides continuityRetainedStateRegistry(),
    ) {
        CircuitCompositionLocals(circuit) {
            LinkdingTheme {
                Root(backstack, linkdingNavigator, apiConfiguration, modifier)
            }
        }
    }
}


private class LinkdingNavigator(
    private val navigator: Navigator,
    private val backStack: SaveableBackStack,
    private val onOpenUrl: (String) -> Unit,
    private val logger: Logger,
) : Navigator {
    override fun goTo(screen: Screen) {
        logger.d { "goTo. Screen: $screen. Current stack: ${backStack.toList()}" }

        when (screen) {
            is UrlScreen -> onOpenUrl(screen.url)
            else -> navigator.goTo(screen)
        }
    }

    override fun pop(): Screen? {
        logger.d { "pop. Current stack: ${backStack.toList()}" }
        return navigator.pop()
    }

    override fun resetRoot(newRoot: Screen): List<Screen> {
        logger.d { "resetRoot: newRoot:$newRoot. Current stack: ${backStack.toList()}" }
        return navigator.resetRoot(newRoot)
    }
}
