package dev.avatsav.linkding.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.slack.circuit.backstack.SaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.CircuitContent
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.runtime.Navigator
import com.slack.circuitx.gesturenavigation.GestureNavigationDecorationFactory
import dev.avatsav.linkding.auth.api.AuthState
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.inject.ComponentHolder
import dev.avatsav.linkding.inject.UserComponent

@Composable
fun AppContent(
  authState: AuthState,
  circuit: Circuit,
  backStack: SaveableBackStack,
  navigator: Navigator,
  modifier: Modifier = Modifier,
) {
  when (authState) {
    is AuthState.Authenticated -> {
      AuthenticatedContent(authState.apiConfig, circuit, backStack, navigator, modifier)
    }

    else -> {
      UnauthenticatedContent(circuit, modifier)
    }
  }
}

@Composable
internal fun AuthenticatedContent(
  apiConfig: ApiConfig,
  circuit: Circuit,
  backStack: SaveableBackStack,
  navigator: Navigator,
  modifier: Modifier = Modifier,
) {
  val userComponent =
    remember(apiConfig) {
      ComponentHolder.component<UserComponent.Factory>().create(apiConfig).also { component ->
        ComponentHolder.updateComponent(component)
      }
    }

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
      navigator = navigator,
      decoratorFactory = GestureNavigationDecorationFactory(onBackInvoked = navigator::pop),
      modifier = modifier.fillMaxSize(),
    )
  }
}

@Composable
internal fun UnauthenticatedContent(circuit: Circuit, modifier: Modifier = Modifier) {
  CircuitCompositionLocals(circuit) { CircuitContent(screen = AuthScreen, modifier = modifier) }
}
