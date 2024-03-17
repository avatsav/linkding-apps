package dev.avatsav.linkding.ui.settings

import androidx.compose.runtime.Composable
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.ui.SettingsScreen
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class SettingsUiPresenterFactory(
    private val presenterFactory: (Navigator) -> SettingsPresenter,
) : Presenter.Factory {
    override fun create(
        screen: Screen,
        navigator: Navigator,
        context: CircuitContext,
    ): Presenter<*>? {
        return when (screen) {
            is SettingsScreen -> presenterFactory(navigator)
            else -> null
        }
    }
}

@Inject
class SettingsPresenter(
    @Assisted private val navigator: Navigator,
    private val logger: Logger,
) : Presenter<SettingsUiState> {

    @Composable
    override fun present(): SettingsUiState {
        return SettingsUiState { event ->
            when (event) {
                SettingsUiEvent.Close -> navigator.pop()
            }
        }
    }
}
