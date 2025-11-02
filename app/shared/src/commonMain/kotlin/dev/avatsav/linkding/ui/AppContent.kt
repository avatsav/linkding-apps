package dev.avatsav.linkding.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.CircuitContent
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.foundation.rememberCircuitNavigator
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuitx.gesturenavigation.GestureNavigationDecorationFactory
import dev.avatsav.linkding.auth.api.AuthState
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.data.model.app.LaunchMode
import dev.avatsav.linkding.inject.ComponentHolder
import dev.avatsav.linkding.inject.UserComponent

@Composable
fun AppContent(
  launchMode: LaunchMode,
  authState: AuthState,
  circuit: Circuit,
  onOpenUrl: (String) -> Boolean,
  onRootPop: () -> Unit,
  modifier: Modifier = Modifier,
) {
  when (authState) {
    is AuthState.Authenticated -> {
      AuthenticatedContent(
        apiConfig = authState.apiConfig,
        launchMode = launchMode,
        circuit = circuit,
        onOpenUrl = onOpenUrl,
        onRootPop = onRootPop,
        modifier = modifier,
      )
    }

    else -> {
      UnauthenticatedContent(circuit, modifier)
    }
  }
}

@Composable
internal fun AuthenticatedContent(
  apiConfig: ApiConfig,
  launchMode: LaunchMode,
  circuit: Circuit,
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

  val backStack = rememberSaveableBackStack(root = launchMode.startScreen())
  val navigator = rememberCircuitNavigator(backStack) { onRootPop() }
  val appNavigator = remember(navigator) { AppNavigator(navigator, onOpenUrl) }

  val userScopedCircuit =
    remember(userComponent) {
      circuit
        .newBuilder()
        .addPresenterFactories(userComponent.presenterFactories)
        .addUiFactories(userComponent.uiFactories)
        .build()
    }

  CircuitCompositionLocals(userScopedCircuit) {
    NavigableCircuitContent(
      backStack = backStack,
      navigator = appNavigator,
      decoratorFactory = GestureNavigationDecorationFactory(onBackInvoked = navigator::pop),
      modifier = modifier.fillMaxSize(),
    )
  }
}

@Composable
internal fun UnauthenticatedContent(circuit: Circuit, modifier: Modifier = Modifier) {
  CircuitCompositionLocals(circuit) { CircuitContent(screen = AuthScreen, modifier = modifier) }
}

private fun LaunchMode.startScreen(): Screen =
  when (this) {
    LaunchMode.Normal -> BookmarksScreen
    is LaunchMode.SharedLink -> AddBookmarkScreen(this.sharedLink)
  }
