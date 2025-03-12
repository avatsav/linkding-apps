package dev.avatsav.linkding.settings.ui

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.data.model.prefs.AppTheme

@Immutable
data class SettingsUiState(
  val appInfo: AppInfo,
  val apiConfig: ApiConfig?,
  val appTheme: AppTheme,
  val useDynamicColors: Boolean,
  val eventSink: (SettingsUiEvent) -> Unit,
) : CircuitUiState

sealed interface SettingsUiEvent : CircuitUiEvent {
  data object Close : SettingsUiEvent

  data class SetAppTheme(val appTheme: AppTheme) : SettingsUiEvent

  data object ToggleUseDynamicColors : SettingsUiEvent

  data object ResetApiConfig : SettingsUiEvent

  data object ShowSourceCode : SettingsUiEvent

  data object ShowLicenses : SettingsUiEvent

  data object ShowPrivacyPolicy : SettingsUiEvent
}
