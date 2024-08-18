package dev.avatsav.linkding.data.auth

import dev.avatsav.linkding.data.auth.AuthState.Authenticated
import dev.avatsav.linkding.data.auth.AuthState.Unauthenticated
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.prefs.AppPreferences
import me.tatarka.inject.annotations.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

sealed interface AuthState {
    data class Authenticated(val apiConfig: ApiConfig) : AuthState
    data object Unauthenticated : AuthState
}

interface AuthManager {
    val state: Flow<AuthState>
    fun getCurrentState(): AuthState
}

@Inject
@AppScope
class DefaultAuthManager(private val appPreferences: AppPreferences) : AuthManager {
    override val state: Flow<AuthState> = appPreferences.observeApiConfig()
        .distinctUntilChanged()
        .map { it.toAuthState() }

    override fun getCurrentState(): AuthState = appPreferences.getApiConfig().toAuthState()

    private fun ApiConfig?.toAuthState(): AuthState =
        this?.let { Authenticated(this) } ?: Unauthenticated
}
