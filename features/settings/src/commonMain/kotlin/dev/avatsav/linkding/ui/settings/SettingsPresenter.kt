package dev.avatsav.linkding.ui.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.r0adkll.kimchi.circuit.annotations.CircuitInject
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.inject.UserScope
import dev.avatsav.linkding.prefs.AppPreferences
import dev.avatsav.linkding.ui.SettingsScreen
import dev.avatsav.linkding.ui.SetupScreen
import dev.avatsav.linkding.ui.UrlScreen
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject
import kotlinx.coroutines.launch

@CircuitInject(SettingsScreen::class, UserScope::class)
@Inject
class SettingsPresenter(
    @Assisted private val navigator: Navigator,
    private val preferences: AppPreferences,
    private val appInfo: AppInfo,
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
