package dev.avatsav.linkding.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.overlay.ContentWithOverlays
import com.slack.circuit.retained.LocalRetainedStateRegistry
import com.slack.circuit.retained.lifecycleRetainedStateRegistry
import com.slack.circuit.runtime.screen.Screen
import dev.avatsav.linkding.auth.api.AuthManager
import dev.avatsav.linkding.auth.api.AuthState
import dev.avatsav.linkding.data.model.app.LaunchMode
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.inject.UiScope
import dev.avatsav.linkding.prefs.AppPreferences
import dev.avatsav.linkding.ui.theme.LinkdingTheme
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

interface AppUi {
  @Composable
  fun Content(
    launchMode: LaunchMode,
    onOpenUrl: (String) -> Boolean,
    onRootPop: () -> Unit,
    modifier: Modifier,
  )
}

@ContributesBinding(UiScope::class)
@SingleIn(UiScope::class)
@Inject
class DefaultAppUi(
  private val circuit: Circuit,
  private val preferences: AppPreferences,
  private val authManager: AuthManager,
) : AppUi {

  @Composable
  override fun Content(
    launchMode: LaunchMode,
    onOpenUrl: (String) -> Boolean,
    onRootPop: () -> Unit,
    modifier: Modifier,
  ) {
    val authState by authManager.state.collectAsState(authManager.getCurrentState())
    val backStack = rememberSaveableBackStack(root = authState.rootScreen(launchMode))
    val navigator = rememberCircuitNavigator(backStack) { onRootPop() }
    val appNavigator = remember(navigator) { AppNavigator(navigator, onOpenUrl) }

    CompositionLocalProvider(LocalRetainedStateRegistry provides lifecycleRetainedStateRegistry()) {
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

private fun AuthState.rootScreen(launchMode: LaunchMode): Screen =
  when (this) {
    is AuthState.Authenticated -> launchMode.authenticatedStartScreen()
    is AuthState.Unauthenticated -> AuthScreen
    is AuthState.Loading -> SplashScreen
  }

private fun LaunchMode.authenticatedStartScreen(): Screen =
  when (this) {
    LaunchMode.Normal -> BookmarksScreen
    is LaunchMode.SharedLink -> AddBookmarkScreen(this.sharedLink)
  }
