package dev.avatsav.linkding.ui.root

import androidx.compose.runtime.Composable
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.avatsav.linkding.ui.RootScreen
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
class RootPresenter : Presenter<RootUiState> {

    @Composable
    override fun present(): RootUiState = RootUiState
}
