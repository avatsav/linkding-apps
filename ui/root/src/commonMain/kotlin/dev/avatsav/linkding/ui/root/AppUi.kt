package dev.avatsav.linkding.ui.root

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.slack.circuit.backstack.SaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.overlay.ContentWithOverlays
import com.slack.circuit.retained.LocalRetainedStateRegistry
import com.slack.circuit.retained.continuityRetainedStateRegistry
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.PopResult
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuitx.gesturenavigation.GestureNavigationDecoration
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.prefs.AppPreferences
import dev.avatsav.linkding.ui.UrlScreen
import dev.avatsav.linkding.ui.theme.LinkdingTheme
import me.tatarka.inject.annotations.Inject
import kotlinx.collections.immutable.ImmutableList

interface AppUi {
    @Composable
    fun Content(
        backStack: SaveableBackStack,
        navigator: Navigator,
        onOpenUrl: (String) -> Boolean,
        modifier: Modifier,
    )
}

@Inject
class DefaultAppUi(
    private val preferences: AppPreferences,
    private val circuit: Circuit,
    private val logger: Logger,
) : AppUi {

    @Composable
    override fun Content(
        backStack: SaveableBackStack,
        navigator: Navigator,
        onOpenUrl: (String) -> Boolean,
        modifier: Modifier,
    ) {
        val linkdingNavigator: Navigator = remember(navigator) {
            LinkdingNavigator(navigator, onOpenUrl, logger)
        }
        CompositionLocalProvider(
            LocalRetainedStateRegistry provides continuityRetainedStateRegistry(),
        ) {
            CircuitCompositionLocals(circuit) {
                LinkdingTheme(
                    darkTheme = preferences.shouldUseDarkTheme(),
                    dynamicColors = preferences.shouldUseDynamicColors(),
                ) {
                    ContentWithOverlays {
                        NavigableCircuitContent(
                            navigator = linkdingNavigator,
                            backStack = backStack,
                            decoration = GestureNavigationDecoration(onBackInvoked = navigator::pop),
                            modifier = modifier.fillMaxSize(),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun AppPreferences.shouldUseDarkTheme(): Boolean {
    val appTheme = remember { observeAppTheme() }.collectAsState(initial = AppTheme.System)
    return when (appTheme.value) {
        AppTheme.System -> isSystemInDarkTheme()
        AppTheme.Light -> false
        AppTheme.Dark -> true
    }
}

@Composable
private fun AppPreferences.shouldUseDynamicColors(): Boolean =
    remember { observeUseDynamicColors() }.collectAsState(initial = true).value

private class LinkdingNavigator(
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
