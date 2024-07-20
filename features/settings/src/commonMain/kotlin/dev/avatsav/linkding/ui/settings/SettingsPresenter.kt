package dev.avatsav.linkding.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.prefs.AppPreferences
import dev.avatsav.linkding.ui.SettingsScreen
import dev.avatsav.linkding.ui.SetupScreen
import dev.avatsav.linkding.ui.UrlScreen
import kotlinx.coroutines.launch
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
    private val preferences: AppPreferences,
    private val appInfo: AppInfo,
    private val logger: Logger,
) : Presenter<SettingsUiState> {

    @Composable
    override fun present(): SettingsUiState {
        val coroutineScope = rememberCoroutineScope()

        val apiConfig by remember { preferences.observeApiConfig() }
            .collectAsRetainedState(preferences.getApiConfig())

        val useDynamicColors by remember { preferences.observeUseDynamicColors() }
            .collectAsRetainedState(false)

        val appTheme by remember { preferences.observeAppTheme() }
            .collectAsRetainedState(AppTheme.System)

        return SettingsUiState(
            appInfo,
            apiConfig,
            appTheme,
            useDynamicColors,
        ) { event ->
            when (event) {
                SettingsUiEvent.Close -> navigator.pop()
                is SettingsUiEvent.SetAppTheme -> coroutineScope.launch {
                    preferences.setAppTheme(event.appTheme)
                }

                SettingsUiEvent.ToggleUseDynamicColors -> coroutineScope.launch {
                    preferences.toggleUseDynamicColors()
                }

                SettingsUiEvent.ResetApiConfig -> coroutineScope.launch {
                    preferences.setApiConfig(null)
                    navigator.goTo(SetupScreen)
                    navigator.resetRoot(SetupScreen)
                }

                SettingsUiEvent.ShowSourceCode -> navigator.goTo(UrlScreen("https://github.com/avatsav/linkding-apps"))
                SettingsUiEvent.ShowLicenses -> {}
                SettingsUiEvent.ShowPrivacyPolicy -> {}
            }
        }
    }
}
