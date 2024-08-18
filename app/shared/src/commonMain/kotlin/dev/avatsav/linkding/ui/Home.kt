package dev.avatsav.linkding.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.slack.circuit.backstack.SaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.CircuitContent
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.runtime.Navigator
import com.slack.circuitx.gesturenavigation.GestureNavigationDecoration
import dev.avatsav.linkding.SharedUserComponent
import dev.avatsav.linkding.UserComponentFactory
import dev.avatsav.linkding.data.auth.AuthState
import dev.avatsav.linkding.data.model.ApiConfig

@Composable
fun Home(
    authState: AuthState?,
    circuit: Circuit,
    backStack: SaveableBackStack,
    navigator: Navigator,
    userComponentFactory: UserComponentFactory,
    modifier: Modifier = Modifier,
) {
    when (authState) {
        is AuthState.Authenticated -> {
            AuthenticatedContent(
                apiConfig = authState.apiConfig,
                backStack,
                navigator,
                userComponentFactory,
                modifier,
            )
        }

        is AuthState.Unauthenticated -> {
            CircuitCompositionLocals(circuit) {
                CircuitContent(
                    screen = SetupScreen,
                    modifier = modifier,
                )
            }
        }

        else -> {
            CircuitCompositionLocals(circuit) {
                CircuitContent(
                    screen = RootScreen(null),
                    modifier = modifier,
                )
            }
        }
    }
}

@Composable
private fun AuthenticatedContent(
    apiConfig: ApiConfig,
    backStack: SaveableBackStack,
    navigator: Navigator,
    userComponentFactory: UserComponentFactory,
    modifier: Modifier = Modifier,
) {
    val userComponent: SharedUserComponent = remember(apiConfig) {
        userComponentFactory(apiConfig)
    }

    LaunchedEffect(Unit) {
        navigator.goToAndResetRoot(BookmarksScreen)
    }

    userComponent.authenticatedContent { circuit ->
        CircuitCompositionLocals(circuit) {
            NavigableCircuitContent(
                backStack = backStack,
                navigator = navigator,
                decoration = GestureNavigationDecoration(onBackInvoked = navigator::pop),
                modifier = modifier.fillMaxSize(),
            )
        }
    }
}
