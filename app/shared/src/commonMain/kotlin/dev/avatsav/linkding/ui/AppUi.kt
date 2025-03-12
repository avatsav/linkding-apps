package dev.avatsav.linkding.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.slack.circuit.backstack.SaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.overlay.ContentWithOverlays
import com.slack.circuit.retained.LocalRetainedStateRegistry
import com.slack.circuit.retained.continuityRetainedStateRegistry
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.screen.Screen
import dev.avatsav.linkding.auth.api.AuthManager
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.inject.qualifier.Unauthenticated
import dev.avatsav.linkding.prefs.AppPreferences
import dev.avatsav.linkding.ui.theme.LinkdingTheme
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

interface AppUi {
  @Composable
  fun Content(
    backStack: SaveableBackStack,
    navigator: Navigator,
    onOpenUrl: (String) -> Boolean,
    modifier: Modifier,
  )
}

@ContributesBinding(UiScope::class)
@SingleIn(UiScope::class)
@Inject
class DefaultAppUi(
  @Unauthenticated private val circuit: Circuit,
  private val preferences: AppPreferences,
  private val authManager: AuthManager,
) : AppUi {

  @Composable
  override fun Content(
    backStack: SaveableBackStack,
    navigator: Navigator,
    onOpenUrl: (String) -> Boolean,
    modifier: Modifier,
  ) {
    val appNavigator: Navigator = remember(navigator) { AppNavigator(navigator, onOpenUrl) }

    val authState by authManager.state.collectAsState(null)

    CompositionLocalProvider(
      LocalRetainedStateRegistry provides continuityRetainedStateRegistry()
    ) {
      LinkdingTheme(
        darkTheme = preferences.shouldUseDarkTheme(),
        dynamicColors = preferences.shouldUseDynamicColors(),
      ) {
        ContentWithOverlays {
          AppContent(
            authState = authState,
            circuit = circuit,
            backStack = backStack,
            navigator = appNavigator,
            modifier = modifier.fillMaxSize(),
          )
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

fun Navigator.goToAndResetRoot(
  screen: Screen,
  saveState: Boolean = false,
  restoreState: Boolean = false,
) {
  goTo(screen)
  resetRoot(screen, saveState, restoreState)
}
