package dev.avatsav.linkding.data

import arrow.core.continuations.Effect
import arrow.core.continuations.effect
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.serialization.Serializable

@Serializable

sealed class CredentialsState
data class Setup(val credentials: Credentials) : CredentialsState()
object CredentialsNotSetup : CredentialsState()

data class Credentials(val url: String, val apiKey: String)

private const val HostUrlCredentialsKey = "hostUrl"
private const val ApiKeyCredentialsKey = "apiKey"

@OptIn(ExperimentalSettingsApi::class)
class CredentialsStore(private val settings: FlowSettings) {

    val state: Flow<CredentialsState> = combine(
        settings.getStringOrNullFlow(HostUrlCredentialsKey),
        settings.getStringOrNullFlow(ApiKeyCredentialsKey),
    ) { hostUrl: String?, apiKey: String? ->
        if (hostUrl == null || apiKey == null) CredentialsNotSetup
        else Setup(Credentials(hostUrl, apiKey))
    }

    fun get(): Effect<CredentialsNotSetup, Credentials> = effect {
        val hostUrl = settings.getStringOrNull(HostUrlCredentialsKey) ?: shift(CredentialsNotSetup)
        val apiKey = settings.getStringOrNull(ApiKeyCredentialsKey) ?: shift(CredentialsNotSetup)
        Credentials(hostUrl, apiKey)
    }

    suspend fun set(credentials: Credentials) {
        settings.putString(HostUrlCredentialsKey, credentials.url)
        settings.putString(ApiKeyCredentialsKey, credentials.apiKey)
    }
}