package dev.avatsav.linkding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.slack.circuit.backstack.SaveableBackStack
import com.slack.circuit.foundation.Circuit
import com.slack.circuit.foundation.CircuitCompositionLocals
import com.slack.circuit.foundation.CircuitContent
import com.slack.circuit.foundation.NavigableCircuitContent
import com.slack.circuit.runtime.Navigator
import com.slack.circuitx.gesturenavigation.GestureNavigationDecoration
import dev.avatsav.linkding.UserComponent
import dev.avatsav.linkding.auth.api.AuthState
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.inject.ComponentHolder

@Composable
fun AppContent(
    authState: AuthState?,
    circuit: Circuit,
    backStack: SaveableBackStack,
    navigator: Navigator,
    modifier: Modifier = Modifier,
) {
    when (authState) {
        is AuthState.Authenticated -> {
            AuthenticatedContent(
                authState.apiConfig,
                backStack,
                navigator,
                modifier,
            )
        }

        is AuthState.Unauthenticated -> {
            SetupScreen(circuit, modifier)
        }

        else -> {
            SplashScreen(modifier)
        }
    }
}

@Composable
private fun AuthenticatedContent(
    apiConfig: ApiConfig,
    backStack: SaveableBackStack,
    navigator: Navigator,
    modifier: Modifier = Modifier,
) {
    val userComponent = remember(apiConfig) {
        ComponentHolder.component<UserComponent.Factory>()
            .create(apiConfig)
            .also { component ->
                ComponentHolder.updateComponent(component)
            }
    }

    LaunchedEffect(Unit) {
        navigator.goToAndResetRoot(BookmarksScreen)
    }

    CircuitCompositionLocals(userComponent.circuit) {
        NavigableCircuitContent(
            backStack = backStack,
            navigator = navigator,
            decoration = GestureNavigationDecoration(onBackInvoked = navigator::pop),
            modifier = modifier.fillMaxSize(),
        )
    }
}

@Composable
private fun SetupScreen(
    circuit: Circuit,
    modifier: Modifier = Modifier,
) {
    CircuitCompositionLocals(circuit) {
        CircuitContent(
            screen = AuthScreen,
            modifier = modifier,
        )
    }
}

@Composable
private fun SplashScreen(
    modifier: Modifier = Modifier,
) {
    Scaffold(
        modifier = modifier,
        content = { padding ->
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(padding).background(MaterialTheme.colorScheme.surface)
                    .fillMaxSize(),
            ) {
                CircularProgressIndicator()
            }
        },
    )
}
