package dev.avatsav.linkding.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.settings.api.SettingsManager
import dev.avatsav.linkding.settings.ui.SettingsUiEvent.Close
import dev.avatsav.linkding.settings.ui.SettingsUiEvent.ResetApiConfig
import dev.avatsav.linkding.settings.ui.SettingsUiEvent.SetAppTheme
import dev.avatsav.linkding.settings.ui.SettingsUiEvent.ShowLicenses
import dev.avatsav.linkding.settings.ui.SettingsUiEvent.ShowSourceCode
import dev.avatsav.linkding.settings.ui.SettingsUiEvent.ToggleUseDynamicColors
import dev.avatsav.linkding.presenter.MoleculePresenter
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Inject
class SettingsPresenter(
  private val settingsManager: SettingsManager,
  private val appInfo: AppInfo,
) : MoleculePresenter<SettingsUiEvent, SettingsUiState, SettingsUiEffect>() {

  @Composable
  override fun models(events: Flow<SettingsUiEvent>): SettingsUiState {
    val apiConfig by
      settingsManager.observeApiConfig().collectAsState(settingsManager.getApiConfig())
    val useDynamicColors by settingsManager.observeUseDynamicColors().collectAsState(false)
    val appTheme by settingsManager.observeAppTheme().collectAsState(AppTheme.System)

    ObserveEvents { event ->
      when (event) {
        Close -> emitEffect(SettingsUiEffect.NavigateUp)
        is SetAppTheme -> presenterScope.launch { settingsManager.setAppTheme(event.appTheme) }

        ToggleUseDynamicColors -> presenterScope.launch { settingsManager.toggleUseDynamicColors() }

        ResetApiConfig ->
          presenterScope.launch {
            settingsManager.setApiConfig(null)
            emitEffect(SettingsUiEffect.ResetToAuth)
          }

        ShowSourceCode ->
          emitEffect(SettingsUiEffect.OpenUrl("https://github.com/avatsav/linkding-apps"))

        ShowLicenses -> {}
        SettingsUiEvent.ShowPrivacyPolicy -> {}
      }
    }

    return SettingsUiState(
      appInfo = appInfo,
      apiConfig = apiConfig,
      appTheme = appTheme,
      useDynamicColors = useDynamicColors,
    )
  }
}
