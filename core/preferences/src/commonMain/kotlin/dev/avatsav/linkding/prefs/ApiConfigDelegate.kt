package dev.avatsav.linkding.prefs

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import dev.avatsav.linkding.data.model.ApiConfig
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

private const val HOST_URL_CONFIGURATION_KEY = "linkding-hostUrl"
private const val API_KEY_CONFIGURATION_KEY = "linkding-apiKey"

internal class ApiConfigDelegate : ReadWriteProperty<DefaultAppPreferences, ApiConfig?> {
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
            thisRef.settings.putString(API_KEY_CONFIGURATION_KEY, value.apiKey)
        } else {
            thisRef.settings.remove(HOST_URL_CONFIGURATION_KEY)
            thisRef.settings.remove(API_KEY_CONFIGURATION_KEY)
        }
    }
}

@OptIn(ExperimentalSettingsApi::class)
internal fun FlowSettings.observeApiConfig(): Flow<ApiConfig?> {
    return combine(
        getStringOrNullFlow(HOST_URL_CONFIGURATION_KEY),
        getStringOrNullFlow(API_KEY_CONFIGURATION_KEY),
    ) { hostUrl: String?, apiKey: String? ->
        if (hostUrl == null || apiKey == null) {
            null
        } else {
            ApiConfig(hostUrl, apiKey)
        }
    }
}
