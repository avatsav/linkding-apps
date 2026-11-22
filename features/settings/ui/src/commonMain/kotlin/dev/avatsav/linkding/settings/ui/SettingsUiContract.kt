package dev.avatsav.linkding.settings.ui

import androidx.compose.runtime.Immutable
import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.internet.LocalNetworkAccess

@Immutable
data class SettingsUiState(
  val appInfo: AppInfo,
  val apiConfig: ApiConfig?,
  val appTheme: AppTheme,
  val useDynamicColors: Boolean,
  val resetBlocked: Boolean = false,
  val localNetworkAccess: LocalNetworkAccess.State = LocalNetworkAccess.State.NotRequired,
)

sealed interface SettingsUiEvent {
  data object Close : SettingsUiEvent

  data class SetAppTheme(val appTheme: AppTheme) : SettingsUiEvent

  data object ToggleUseDynamicColors : SettingsUiEvent

  data object ResetApiConfig : SettingsUiEvent

  data object RequestLocalNetworkAccess : SettingsUiEvent

  data object RefreshLocalNetworkAccess : SettingsUiEvent

  data object ShowSourceCode : SettingsUiEvent

  data object ShowLicenses : SettingsUiEvent

  data object ShowPrivacyPolicy : SettingsUiEvent
}

sealed interface SettingsUiEffect {
  data object NavigateUp : SettingsUiEffect

  data object RequestLocalNetworkAccess : SettingsUiEffect

  data object ResetToAuth : SettingsUiEffect

  data class OpenUrl(val url: String) : SettingsUiEffect
}
