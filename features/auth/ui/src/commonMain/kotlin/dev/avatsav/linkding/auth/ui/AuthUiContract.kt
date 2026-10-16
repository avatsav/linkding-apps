package dev.avatsav.linkding.auth.ui

import androidx.compose.runtime.Immutable

@Immutable
data class AuthUiState(
    val verifying: Boolean = false,
    val saving: Boolean = false,
    val invalidHostUrl: Boolean = false,
    val invalidApiKey: Boolean = false,
    val errorMessage: String? = null,
    val localNetworkAccessRequired: Boolean = false,
) {
  val loading: Boolean
    get() = verifying || saving
}

sealed interface AuthUiEvent {
  data class SaveCredentials(val hostUrl: String, val apiKey: String) : AuthUiEvent

  data class LocalNetworkPermissionResult(val granted: Boolean) : AuthUiEvent
}

sealed interface AuthEffect {
  data object AuthenticationSuccess : AuthEffect

  data object RequestLocalNetworkAccess : AuthEffect
}
