package dev.avatsav.linkding.auth.api

import dev.avatsav.linkding.data.model.ApiConfig
import kotlinx.coroutines.flow.Flow

sealed interface AuthState {
    data class Authenticated(val apiConfig: ApiConfig) : AuthState
    data object Unauthenticated : AuthState
}

interface AuthManager {
    val state: Flow<AuthState>
    fun getCurrentState(): AuthState
}
