package dev.avatsav.linkding.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.savedstate.serialization.SavedStateConfiguration
import dev.avatsav.linkding.auth.api.AuthState
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.data.model.app.LaunchMode
import dev.avatsav.linkding.di.ComponentHolder
import dev.avatsav.linkding.di.UserComponent
import dev.avatsav.linkding.navigation.AddBookmarkScreen
import dev.avatsav.linkding.navigation.BookmarksScreen

@Composable
fun AppContent(
  launchMode: LaunchMode,
  authState: AuthState,
  savedStateConfiguration: SavedStateConfiguration,
  onOpenUrl: (String) -> Boolean,
  onRootPop: () -> Unit,
  modifier: Modifier = Modifier,
) {
  TODO()
}

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

// TODO: Use deeplinks
private fun LaunchMode.startScreen(): NavKey =
  when (this) {
    LaunchMode.Normal -> BookmarksScreen
    is LaunchMode.SharedLink -> AddBookmarkScreen(this.sharedLink)
  }
