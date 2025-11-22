package dev.avatsav.linkding.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import dev.avatsav.linkding.auth.api.AuthManager
import dev.avatsav.linkding.auth.api.AuthState
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.data.model.app.LaunchMode
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.di.ComponentHolder
import dev.avatsav.linkding.di.UserComponent
import dev.avatsav.linkding.di.scope.UiScope
import dev.avatsav.linkding.navigation.AddBookmarkScreen
import dev.avatsav.linkding.navigation.BookmarksScreen
import dev.avatsav.linkding.prefs.AppPreferences
import dev.avatsav.linkding.ui.theme.LinkdingTheme
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn

interface AppUi {
  @Composable
  fun Content(launchMode: LaunchMode, onOpenUrl: (String) -> Boolean, modifier: Modifier)
}

@ContributesBinding(UiScope::class)
@SingleIn(UiScope::class)
@Inject
class DefaultAppUi(
  private val preferences: AppPreferences,
  private val authManager: AuthManager,
  private val savedStateConfiguration: SavedStateConfiguration,
) : AppUi {

  @Composable
  override fun Content(launchMode: LaunchMode, onOpenUrl: (String) -> Boolean, modifier: Modifier) {
    val authState by authManager.state.collectAsState(authManager.getCurrentState())
    LinkdingTheme(
      darkTheme = preferences.shouldUseDarkTheme(),
      dynamicColors = preferences.shouldUseDynamicColors(),
    ) {
      AppContent(
        launchMode = launchMode,
        authState = authState,
        onOpenUrl = onOpenUrl,
        modifier = modifier.fillMaxSize(),
        savedStateConfiguration = savedStateConfiguration,
      )
    }
  }
}

@Composable
fun AppContent(
  launchMode: LaunchMode,
  authState: AuthState,
  savedStateConfiguration: SavedStateConfiguration,
  onOpenUrl: (String) -> Boolean,
  modifier: Modifier = Modifier,
) {}

@Composable
internal fun AuthenticatedContent(
  apiConfig: ApiConfig,
  launchMode: LaunchMode,
  onOpenUrl: (String) -> Boolean,
  onRootPop: () -> Unit,
  modifier: Modifier = Modifier,
) {
  val userComponent =
    remember(apiConfig) {
      ComponentHolder.component<UserComponent.Factory>().create(apiConfig).also { component ->
        ComponentHolder.updateComponent(component)
      }
    }

  TODO()
}

private fun LaunchMode.startScreen(): NavKey =
  when (this) {
    LaunchMode.Normal -> BookmarksScreen
    is LaunchMode.SharedLink -> AddBookmarkScreen(this.sharedLink)
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
