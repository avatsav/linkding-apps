package dev.avatsav.linkding.ui.settings

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState

@Immutable
data class SettingsUiState(
    val eventSink: (SettingsUiEvent) -> Unit,
) : CircuitUiState

sealed interface SettingsUiEvent : CircuitUiEvent {
    data object Close : SettingsUiEvent
}
