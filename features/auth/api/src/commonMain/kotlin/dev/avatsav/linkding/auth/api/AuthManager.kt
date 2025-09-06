package dev.avatsav.linkding.auth.api

import dev.avatsav.linkding.data.model.ApiConfig
import kotlinx.coroutines.flow.Flow

interface AuthManager {
  val state: Flow<AuthState>

  fun getCurrentState(): AuthState
}

sealed interface AuthState {
  data class Authenticated(val apiConfig: ApiConfig) : AuthState

  data object Unauthenticated : AuthState

  data object Loading : AuthState
}
