package dev.avatsav.linkding.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.github.michaelbull.result.onFailure
import com.github.michaelbull.result.onSuccess
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.domain.interactors.FetchApiConfiguration
import dev.avatsav.linkding.ui.AddBookmarkScreen
import dev.avatsav.linkding.ui.BookmarksScreen
import dev.avatsav.linkding.ui.RootScreen
import dev.avatsav.linkding.ui.SetupScreen
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class RootUiPresenterFactory(
    private val presenterFactory: (Navigator, RootScreen) -> RootPresenter,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? {
        return when (screen) {
            is RootScreen -> presenterFactory(navigator, screen)
            else -> null
        }
    }
}

@Inject
class RootPresenter(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: RootScreen,
    private val fetchApiConfiguration: FetchApiConfiguration,
    private val logger: Logger,
) : Presenter<RootUiState> {

    @Composable
    override fun present(): RootUiState {
        LaunchedEffect(Unit) {
            fetchApiConfiguration(Unit)
                .onSuccess {
                    if (it == null) {
                        navigator.goToAndResetRoot(SetupScreen)
                    } else {
                        if (screen.sharedUrl != null) {
                            navigator.goToAndResetRoot(AddBookmarkScreen(screen.sharedUrl))
                        } else {
                            navigator.goToAndResetRoot(BookmarksScreen)
                        }
                    }
                }.onFailure {
                    navigator.goToAndResetRoot(SetupScreen)
                }
        }
        return RootUiState
    }
}

internal fun Navigator.goToAndResetRoot(
    screen: Screen,
    saveState: Boolean = false,
    restoreState: Boolean = false,
) {
    goTo(screen)
    resetRoot(screen, saveState, restoreState)
}
