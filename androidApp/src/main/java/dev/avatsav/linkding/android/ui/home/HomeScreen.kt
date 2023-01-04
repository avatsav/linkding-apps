package dev.avatsav.linkding.android.ui.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.avatsav.linkding.android.SplashScreen
import dev.avatsav.linkding.android.ui.destinations.BookmarksScreenDestination
import dev.avatsav.linkding.ui.Fail
import dev.avatsav.linkding.ui.presenter.HomePresenter
import dev.avatsav.linkding.ui.presenter.HomeViewState
import dev.avatsav.linkding.ui.Loading
import dev.avatsav.linkding.ui.Success
import dev.avatsav.linkding.ui.Uninitialized
import org.koin.androidx.compose.get

@Composable
@RootNavGraph(start = true)
@Destination
fun HomeScreen(navigator: DestinationsNavigator) {
    val presenter: HomePresenter = get()
    DisposableEffect(presenter) {
        onDispose {
            presenter.clear()
        }
    }
    HomeScreen(
        presenter = presenter,
        onSetupSuccess = {
            navigator.popBackStack()
            navigator.navigate(BookmarksScreenDestination)
        })
}

@Composable
private fun HomeScreen(
    presenter: HomePresenter,
    onSetupSuccess: () -> Unit,
) {
    val uiState by presenter.uiState.collectAsState()
    HomeScreen(uiState = uiState,
        onSetupSuccess = onSetupSuccess,
        onConfigurationSubmitted = { url, apiKey ->
            presenter.setConfiguration(url, apiKey)
        })
}

@Composable
private fun HomeScreen(
    uiState: HomeViewState,
    onSetupSuccess: () -> Unit,
    onConfigurationSubmitted: (String, String) -> Unit
) {
    when (val setupState = uiState.setupState) {
        is Fail -> SetupConfigurationScreen(
            state = uiState.saveConfigurationState,
            onSaveSuccess = onSetupSuccess,
            onConfigurationSubmitted = onConfigurationSubmitted
        )

        is Success -> if (setupState.value) {
            onSetupSuccess()
        } else {
            SetupConfigurationScreen(
                state = uiState.saveConfigurationState,
                onSaveSuccess = onSetupSuccess,
                onConfigurationSubmitted = onConfigurationSubmitted
            )
        }

        is Loading,
        Uninitialized -> SplashScreen()
    }
}