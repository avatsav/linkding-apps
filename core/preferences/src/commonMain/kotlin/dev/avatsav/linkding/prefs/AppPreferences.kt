package dev.avatsav.linkding.prefs

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.data.model.ApiConfig
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

interface AppPreferences {

    var apiConfiguration: ApiConfig?

    fun observeApiConfiguration(): Flow<ApiConfig?>
}

@OptIn(ExperimentalSettingsApi::class)
internal class DefaultAppPreferences(
    internal val settings: ObservableSettings,
    dispatchers: AppCoroutineDispatchers,
) : AppPreferences {


    private val flowSettings by lazy { settings.toFlowSettings(dispatchers.io) }

    override var apiConfiguration: ApiConfig? by ApiConfigurationDelegate()

    override fun observeApiConfiguration(): Flow<ApiConfig?> {
        return combine(
            flowSettings.getStringOrNullFlow(HOST_URL_CONFIGURATION_KEY),
            flowSettings.getStringOrNullFlow(API_KEY_CONFIGURATION_KEY),
        ) { hostUrl: String?, apiKey: String? ->
            if (hostUrl == null || apiKey == null) {
                null
            } else {
                ApiConfig(hostUrl, apiKey)
            }
        }
    }
}

private class ApiConfigurationDelegate : ReadWriteProperty<DefaultAppPreferences, ApiConfig?> {
    override fun getValue(
        thisRef: DefaultAppPreferences,
        property: KProperty<*>,
    ): ApiConfig? {
        val host = thisRef.settings.getStringOrNull(HOST_URL_CONFIGURATION_KEY)
        val apiKey = thisRef.settings.getStringOrNull(API_KEY_CONFIGURATION_KEY)
        return if (host == null || apiKey == null) {
            null
        } else {
            ApiConfig(host, apiKey)
        }
    }

    override fun setValue(
        thisRef: DefaultAppPreferences,
        property: KProperty<*>,
        value: ApiConfig?,
    ) {
        if (value != null) {
            thisRef.settings.putString(HOST_URL_CONFIGURATION_KEY, value.hostUrl)
            thisRef.settings.putString(API_KEY_CONFIGURATION_KEY, value.hostUrl)
        } else {

            thisRef.settings.remove(HOST_URL_CONFIGURATION_KEY)
            thisRef.settings.remove(API_KEY_CONFIGURATION_KEY)
        }
    }
}

private const val HOST_URL_CONFIGURATION_KEY = "hostUrl"
private const val API_KEY_CONFIGURATION_KEY = "apiKey"
