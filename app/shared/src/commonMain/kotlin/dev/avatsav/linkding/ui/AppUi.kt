package dev.avatsav.linkding.ui

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.AnimatedContentTransitionScope.SlideDirection
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.unveilIn
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.scene.Scene
import androidx.navigation3.ui.NavDisplay
import androidx.navigationevent.NavigationEvent.SwipeEdge
import androidx.savedstate.serialization.SavedStateConfiguration
import dev.avatsav.linkding.auth.api.AuthManager
import dev.avatsav.linkding.auth.api.AuthState
import dev.avatsav.linkding.data.model.app.LaunchMode
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.di.GraphHolder
import dev.avatsav.linkding.di.UserGraph
import dev.avatsav.linkding.di.scope.UiScope
import dev.avatsav.linkding.navigation.BottomSheetSceneStrategy
import dev.avatsav.linkding.navigation.NavigatorCompositionLocals
import dev.avatsav.linkding.navigation.Route
import dev.avatsav.linkding.navigation.RouteEntryProviderScope
import dev.avatsav.linkding.navigation.rememberNavigator
import dev.avatsav.linkding.navigation.rememberRouteBackStack
import dev.avatsav.linkding.prefs.AppPreferences
import dev.avatsav.linkding.ui.theme.LinkdingTheme
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import dev.zacsweers.metrox.viewmodel.MetroViewModelFactory

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
  private val routeEntryScope: Set<RouteEntryProviderScope>,
  private val preferences: AppPreferences,
  private val authManager: AuthManager,
  private val savedStateConfiguration: SavedStateConfiguration,
  private val metroViewModelFactory: MetroViewModelFactory,
) : AppUi {

  @Composable
  override fun Content(
    launchMode: LaunchMode,
    onOpenUrl: (String) -> Boolean,
    onRootPop: () -> Unit,
    modifier: Modifier,
  ) {
    val initialAuthState = remember { authManager.getCurrentState() }
    val startRoute = remember(launchMode) { initialAuthState.startRoute(launchMode) }

    val backStack = rememberRouteBackStack(savedStateConfiguration, startRoute)
    val navigator = rememberNavigator(backStack, onOpenUrl, onRootPop)
    val bottomSheetSceneStrategy = remember { BottomSheetSceneStrategy<Route>() }

    val authState by authManager.state.collectAsState(initialAuthState)
    val viewModelFactory = rememberViewModelFactory(authState, metroViewModelFactory)

    CompositionLocalProvider(LocalMetroViewModelFactory provides viewModelFactory) {
      NavigatorCompositionLocals(navigator) {
        LinkdingTheme(
          darkTheme = preferences.shouldUseDarkTheme(),
          dynamicColors = preferences.shouldUseDynamicColors(),
        ) {
          NavDisplay(
            entryDecorators =
              listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator(),
              ),
            backStack = backStack,
            onBack = { navigator.pop() },
            sceneStrategy = bottomSheetSceneStrategy,
            entryProvider =
              entryProvider(builder = { routeEntryScope.forEach { builder -> this.builder() } }),
            predictivePopTransitionSpec = predictivePopTransitionSpec(),
          )
        }
      }
    }
  }
}

@OptIn(ExperimentalAnimationApi::class)
private fun <T : Any> predictivePopTransitionSpec():
  AnimatedContentTransitionScope<Scene<T>>.(@SwipeEdge Int) -> ContentTransform = { edge ->
  val towards = SlideDirection.Right
  ContentTransform(
    targetContentEnter =
      slideIntoContainer(towards = towards, initialOffset = { it / 4 }) + unveilIn(),
    initialContentExit = slideOutOfContainer(towards = towards),
  )
}

@Composable
private fun rememberViewModelFactory(
  authState: AuthState,
  defaultFactory: MetroViewModelFactory,
): MetroViewModelFactory {
  return when (authState) {
    is Loading,
    is Unauthenticated -> defaultFactory
    is Authenticated -> {
      remember(authState.apiConfig) {
          GraphHolder.graph<UserGraph.Factory>().create(authState.apiConfig).also { component ->
            GraphHolder.updateGraph(component)
          }
        }
        .metroViewModelFactory
    }
  }
}

private fun AuthState.startRoute(launchMode: LaunchMode): Route =
  when (this) {
    is Loading,
    is Unauthenticated -> Route.Auth
    is Authenticated -> launchMode.startRoute()
  }

private fun LaunchMode.startRoute(): Route =
  when (this) {
    Normal -> Route.BookmarksFeed
    is SharedLink -> Route.AddBookmark.Shared(this.sharedLink)
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
