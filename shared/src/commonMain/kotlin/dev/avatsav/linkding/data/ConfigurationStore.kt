package dev.avatsav.linkding.data

import arrow.core.continuations.Effect
import arrow.core.continuations.effect
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.serialization.Serializable

@Serializable

sealed class ConfigurationState
data class Setup(val configuration: Configuration) : ConfigurationState()
object ConfigurationNotSetup : ConfigurationState()

private const val HostUrlConfigurationKey = "hostUrl"
private const val ApiKeyConfigurationKey = "apiKey"

class ConfigurationStore(private val settings: FlowSettings) {

    val state: Flow<ConfigurationState> = combine(
        settings.getStringOrNullFlow(HostUrlConfigurationKey),
        settings.getStringOrNullFlow(ApiKeyConfigurationKey),
    ) { hostUrl: String?, apiKey: String? ->
        if (hostUrl == null || apiKey == null) ConfigurationNotSetup
        else Setup(Configuration(hostUrl, apiKey))
    }

    fun get(): Effect<ConfigurationNotSetup, Configuration> = effect {
        val hostUrl =
            settings.getStringOrNull(HostUrlConfigurationKey) ?: shift(ConfigurationNotSetup)
        val apiKey =
            settings.getStringOrNull(ApiKeyConfigurationKey) ?: shift(ConfigurationNotSetup)
        Configuration(hostUrl, apiKey)
    }

    suspend fun set(configuration: Configuration) {
        settings.putString(HostUrlConfigurationKey, configuration.url)
        settings.putString(ApiKeyConfigurationKey, configuration.apiKey)
    }
}