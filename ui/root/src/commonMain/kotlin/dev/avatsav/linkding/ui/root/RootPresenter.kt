package dev.avatsav.linkding.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.prefs.AppPreferences
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
    ): Presenter<*>? = when (screen) {
        is RootScreen -> presenterFactory(navigator, screen)
        else -> null
    }
}

@Inject
class RootPresenter(
    @Assisted private val navigator: Navigator,
    @Assisted private val screen: RootScreen,
    private val preferences: AppPreferences,
    private val logger: Logger,
) : Presenter<RootUiState> {

    @Composable
    override fun present(): RootUiState {
        LaunchedEffect(Unit) {
            if (preferences.getApiConfig() == null) {
                navigator.goToAndResetRoot(SetupScreen)
            } else {
                if (screen.sharedUrl != null) {
                    navigator.goToAndResetRoot(AddBookmarkScreen(screen.sharedUrl))
                } else {
                    navigator.goToAndResetRoot(BookmarksScreen)
                }
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
