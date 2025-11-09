package dev.avatsav.linkding.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.retained.collectAsRetainedState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.di.scope.UserScope
import dev.avatsav.linkding.settings.api.SettingsManager
import dev.avatsav.linkding.ui.AuthScreen
import dev.avatsav.linkding.ui.SettingsScreen
import dev.avatsav.linkding.ui.UrlScreen
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import kotlinx.coroutines.launch

@AssistedInject
class SettingsPresenter(
  @Assisted private val navigator: Navigator,
  private val settingsManager: SettingsManager,
  private val appInfo: AppInfo,
) : Presenter<SettingsUiState> {

  @CircuitInject(SettingsScreen::class, UserScope::class)
  @AssistedFactory
  interface Factory {
    fun create(navigator: Navigator): SettingsPresenter
  }

  @Composable
  override fun present(): SettingsUiState {
    val coroutineScope = rememberCoroutineScope()

    val apiConfig by
      remember { settingsManager.observeApiConfig() }
        .collectAsRetainedState(settingsManager.getApiConfig())

    val useDynamicColors by
      remember { settingsManager.observeUseDynamicColors() }.collectAsRetainedState(false)

    val appTheme by
      remember { settingsManager.observeAppTheme() }.collectAsRetainedState(AppTheme.System)

    return SettingsUiState(appInfo, apiConfig, appTheme, useDynamicColors) { event ->
      when (event) {
        SettingsUiEvent.Close -> navigator.pop()
        is SettingsUiEvent.SetAppTheme ->
          coroutineScope.launch { settingsManager.setAppTheme(event.appTheme) }

        SettingsUiEvent.ToggleUseDynamicColors ->
          coroutineScope.launch { settingsManager.toggleUseDynamicColors() }

        SettingsUiEvent.ResetApiConfig ->
          coroutineScope.launch {
            settingsManager.setApiConfig(null)
            navigator.goTo(AuthScreen)
            navigator.resetRoot(AuthScreen)
          }

        SettingsUiEvent.ShowSourceCode ->
          navigator.goTo(UrlScreen("https://github.com/avatsav/linkding-apps"))

        SettingsUiEvent.ShowLicenses -> {}
        SettingsUiEvent.ShowPrivacyPolicy -> {}
      }
    }
  }
}
