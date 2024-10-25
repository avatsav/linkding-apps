package dev.avatsav.linkding.auth.ui

import androidx.compose.runtime.Immutable
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState

@Immutable
data class AuthUiState(
    val verifying: Boolean = false,
    val saving: Boolean = false,
    val invalidHostUrl: Boolean = false,
    val invalidApiKey: Boolean = false,
    val errorMessage: String? = null,
    val eventSink: (SetupUiEvent) -> Unit,
) : CircuitUiState {
    val loading: Boolean
        get() = verifying || saving
}

sealed interface SetupUiEvent : CircuitUiEvent {
    data class SaveConfiguration(val hostUrl: String, val apiKey: String) : SetupUiEvent
}
