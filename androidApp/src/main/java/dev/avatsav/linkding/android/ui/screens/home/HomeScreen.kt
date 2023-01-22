package dev.avatsav.linkding.android.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import dev.avatsav.linkding.android.SplashScreen
import dev.avatsav.linkding.android.ui.screens.destinations.BookmarksScreenDestination
import dev.avatsav.linkding.ui.onFail
import dev.avatsav.linkding.ui.onLoading
import dev.avatsav.linkding.ui.onSuccess
import dev.avatsav.linkding.ui.home.HomeViewModel
import dev.avatsav.linkding.ui.home.HomeViewState
import org.koin.androidx.compose.koinViewModel

@Composable
@RootNavGraph(start = true)
@Destination
fun HomeScreen(navigator: DestinationsNavigator) {
    val viewModel: HomeViewModel = koinViewModel()
    HomeScreen(
        viewModel = viewModel,
        onSetupSuccess = {
            navigator.popBackStack()
            navigator.navigate(BookmarksScreenDestination)
        })
}

@Composable
private fun HomeScreen(
    viewModel: HomeViewModel,
    onSetupSuccess: () -> Unit,
) {
    val state by viewModel.state.collectAsState()
    HomeScreen(state = state,
        onSetupSuccess = onSetupSuccess,
        onConfigurationSubmitted = { url, apiKey ->
            viewModel.setConfiguration(url, apiKey)
        })
}

@Composable
private fun HomeScreen(
    state: HomeViewState,
    onSetupSuccess: () -> Unit,
    onConfigurationSubmitted: (String, String) -> Unit
) {
    state.configuration onLoading {
        SplashScreen()
    } onSuccess {
        onSetupSuccess()

    } onFail {
        SetupConfigurationScreen(
            state = state.saveConfigurationState,
            onSaveSuccess = onSetupSuccess,
            onConfigurationSubmitted = onConfigurationSubmitted
        )
    }
}