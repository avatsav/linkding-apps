package dev.avatsav.linkding.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.bookmarks.api.PendingBookmarks
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.presenter.MoleculePresenter
import dev.avatsav.linkding.settings.api.SettingsManager
import dev.avatsav.linkding.settings.ui.SettingsUiEvent.Close
import dev.avatsav.linkding.settings.ui.SettingsUiEvent.ResetApiConfig
import dev.avatsav.linkding.settings.ui.SettingsUiEvent.SetAppTheme
import dev.avatsav.linkding.settings.ui.SettingsUiEvent.ShowLicenses
import dev.avatsav.linkding.settings.ui.SettingsUiEvent.ShowSourceCode
import dev.avatsav.linkding.settings.ui.SettingsUiEvent.ToggleUseDynamicColors
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

@Inject
class SettingsPresenter(
    private val settingsManager: SettingsManager,
    private val appInfo: AppInfo,
    private val pendingBookmarks: PendingBookmarks,
) : MoleculePresenter<SettingsUiEvent, SettingsUiState, SettingsUiEffect>() {

  @Composable
  override fun models(events: Flow<SettingsUiEvent>): SettingsUiState {
    val apiConfig by
        settingsManager.observeApiConfig().collectAsState(settingsManager.getApiConfig())
    val useDynamicColors by settingsManager.observeUseDynamicColors().collectAsState(false)
    val appTheme by settingsManager.observeAppTheme().collectAsState(AppTheme.System)

    var resetBlocked by remember { mutableStateOf(false) }

    ObserveEvents { event ->
      when (event) {
        Close -> emitEffect(SettingsUiEffect.NavigateUp)
        is SetAppTheme -> presenterScope.launch { settingsManager.setAppTheme(event.appTheme) }

        ToggleUseDynamicColors -> presenterScope.launch { settingsManager.toggleUseDynamicColors() }

        ResetApiConfig ->
            presenterScope.launch {
              if (pendingBookmarks.observe().first().isNotEmpty()) {
                resetBlocked = true
              } else {
                settingsManager.setApiConfig(null)
                emitEffect(SettingsUiEffect.ResetToAuth)
              }
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
        resetBlocked = resetBlocked,
    )
  }
}
