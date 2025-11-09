package dev.avatsav.linkding.auth.ui

import androidx.compose.runtime.Immutable

@Immutable
data class AuthUiState(
  val verifying: Boolean = false,
  val saving: Boolean = false,
  val invalidHostUrl: Boolean = false,
  val invalidApiKey: Boolean = false,
  val errorMessage: String? = null,
) {
  val loading: Boolean
    get() = verifying || saving
}

sealed interface AuthUiEvent {
  data class SaveCredentials(val hostUrl: String, val apiKey: String) : AuthUiEvent
}
