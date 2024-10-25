package dev.avatsav.linkding.auth

import com.r0adkll.kimchi.annotations.ContributesBinding
import dev.avatsav.linkding.auth.api.AuthManager
import dev.avatsav.linkding.auth.api.AuthState
import dev.avatsav.linkding.auth.api.AuthState.Authenticated
import dev.avatsav.linkding.auth.api.AuthState.Unauthenticated
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import dev.avatsav.linkding.prefs.AppPreferences
import me.tatarka.inject.annotations.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultAuthManager(private val appPrefs: AppPreferences) : AuthManager {
    override val state: Flow<AuthState> = appPrefs.observeApiConfig()
        .distinctUntilChanged()
        .map { it.toAuthState() }

    override fun getCurrentState(): AuthState = appPrefs.getApiConfig().toAuthState()

    private fun ApiConfig?.toAuthState(): AuthState =
        this?.let { Authenticated(this) } ?: Unauthenticated
}
