package dev.avatsav.linkding.settings.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewModelScope
import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.settings.api.SettingsManager
import dev.avatsav.linkding.viewmodel.MoleculePresenter
import dev.avatsav.linkding.viewmodel.MoleculeViewModel
import dev.zacsweers.metro.Assisted
import dev.zacsweers.metro.AssistedFactory
import dev.zacsweers.metro.AssistedInject
import dev.zacsweers.metro.Inject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

@Inject
class SettingsViewModel(settingsPresenterFactory: SettingsPresenter.Factory) :
  MoleculeViewModel<SettingsUiEvent, SettingsUiState>() {
  override val presenter by lazy { settingsPresenterFactory.create(viewModelScope) }
}

@AssistedInject
class SettingsPresenter(
  @Assisted coroutineScope: CoroutineScope,
  private val settingsManager: SettingsManager,
  private val appInfo: AppInfo,
  private val navigator: SettingsNavigator,
) : MoleculePresenter<SettingsUiEvent, SettingsUiState>(coroutineScope) {

  @Composable
  override fun models(events: Flow<SettingsUiEvent>): SettingsUiState {
    val apiConfig by
      settingsManager.observeApiConfig().collectAsState(settingsManager.getApiConfig())
    val useDynamicColors by settingsManager.observeUseDynamicColors().collectAsState(false)
    val appTheme by settingsManager.observeAppTheme().collectAsState(AppTheme.System)

    ObserveEvents { event ->
      when (event) {
        SettingsUiEvent.Close -> navigator.navigateUp()
        is SettingsUiEvent.SetAppTheme ->
          presenterScope.launch { settingsManager.setAppTheme(event.appTheme) }

        SettingsUiEvent.ToggleUseDynamicColors ->
          presenterScope.launch { settingsManager.toggleUseDynamicColors() }

        SettingsUiEvent.ResetApiConfig ->
          presenterScope.launch {
            settingsManager.setApiConfig(null)
            navigator.resetToAuth()
          }

        SettingsUiEvent.ShowSourceCode ->
          navigator.navigateToUrl("https://github.com/avatsav/linkding-apps")

        SettingsUiEvent.ShowLicenses -> {}
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

  @AssistedFactory
  interface Factory {
    fun create(coroutineScope: CoroutineScope): SettingsPresenter
  }
}

interface SettingsNavigator {
  fun navigateUp()

  fun resetToAuth()

  fun navigateToUrl(url: String)
}
