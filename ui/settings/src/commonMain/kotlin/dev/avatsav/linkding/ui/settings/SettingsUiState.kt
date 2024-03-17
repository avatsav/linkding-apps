package dev.avatsav.linkding.ui.settings

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.data.model.prefs.AppTheme

@Immutable
data class SettingsUiState(
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
}
