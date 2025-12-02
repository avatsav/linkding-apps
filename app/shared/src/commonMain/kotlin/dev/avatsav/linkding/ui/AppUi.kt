package dev.avatsav.linkding.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import androidx.savedstate.serialization.SavedStateConfiguration
import dev.avatsav.linkding.auth.api.AuthManager
import dev.avatsav.linkding.auth.api.AuthState
import dev.avatsav.linkding.data.model.app.LaunchMode
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.di.GraphHolder
import dev.avatsav.linkding.di.UserGraph
import dev.avatsav.linkding.di.scope.UiScope
import dev.avatsav.linkding.navigation.LocalNavigator
import dev.avatsav.linkding.navigation.Screen
import dev.avatsav.linkding.navigation.ScreenEntryProviderScope
import dev.avatsav.linkding.navigation.rememberNavigator
import dev.avatsav.linkding.prefs.AppPreferences
import dev.avatsav.linkding.ui.theme.LinkdingTheme
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory

interface AppUi {
  @Composable
  fun Content(launchMode: LaunchMode, onOpenUrl: (String) -> Boolean, modifier: Modifier)
}

@ContributesBinding(UiScope::class)
@SingleIn(UiScope::class)
@Inject
class DefaultAppUi(
  private val screenEntryScope: Set<ScreenEntryProviderScope>,
  private val preferences: AppPreferences,
  private val authManager: AuthManager,
  private val savedStateConfiguration: SavedStateConfiguration,
  private val metroViewModelFactory: MetroViewModelFactory,
) : AppUi {

  @Composable
  override fun Content(launchMode: LaunchMode, onOpenUrl: (String) -> Boolean, modifier: Modifier) {
    val authState by authManager.state.collectAsState(authManager.getCurrentState())

    val viewModelFactory = rememberViewModelFactory(authState, metroViewModelFactory)
    val startScreen =
      remember(authState, launchMode) {
        when (authState) {
          is AuthState.Loading,
          is AuthState.Unauthenticated -> Screen.Auth
          is AuthState.Authenticated -> launchMode.startScreen()
        }
      }

    val backStack = rememberNavBackStack(savedStateConfiguration, startScreen)
    val navigator = rememberNavigator(backStack)

    CompositionLocalProvider(
      LocalMetroViewModelFactory provides viewModelFactory,
      LocalNavigator provides navigator,
    ) {
      LinkdingTheme(
        darkTheme = preferences.shouldUseDarkTheme(),
        dynamicColors = preferences.shouldUseDynamicColors(),
      ) {
        NavDisplay(
          backStack = backStack,
          onBack = { navigator.pop() },
          entryProvider =
            entryProvider(builder = { screenEntryScope.forEach { builder -> this.builder() } }),
        )
      }
    }
  }
}

@Composable
private fun rememberViewModelFactory(
  authState: AuthState,
  defaultFactory: MetroViewModelFactory,
): MetroViewModelFactory {
  return when (authState) {
    is AuthState.Loading,
    is AuthState.Unauthenticated -> {
      defaultFactory
    }
    is AuthState.Authenticated -> {
      remember(authState.apiConfig) {
          GraphHolder.graph<UserGraph.Factory>().create(authState.apiConfig).also { component ->
            GraphHolder.updateGraph(component)
          }
        }
        .metroViewModelFactory
    }
  }
}

private fun LaunchMode.startScreen(): Screen =
  when (this) {
    LaunchMode.Normal -> Screen.BookmarksFeed
    is LaunchMode.SharedLink -> Screen.AddBookmark(this.sharedLink)
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
