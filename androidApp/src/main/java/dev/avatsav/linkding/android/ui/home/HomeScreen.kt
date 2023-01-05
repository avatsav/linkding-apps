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
import dev.avatsav.linkding.ui.onFail
import dev.avatsav.linkding.ui.onLoading
import dev.avatsav.linkding.ui.onSuccess
import dev.avatsav.linkding.ui.presenter.HomePresenter
import dev.avatsav.linkding.ui.presenter.HomeViewState
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
    uiState.configuration onLoading {
        SplashScreen()
    } onSuccess {
        onSetupSuccess()

    } onFail {
        SetupConfigurationScreen(
            state = uiState.saveConfigurationState,
            onSaveSuccess = onSetupSuccess,
            onConfigurationSubmitted = onConfigurationSubmitted
        )
    }
}