package dev.avatsav.linkding.prefs

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import dev.avatsav.linkding.AppCoroutineDispatchers
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import me.tatarka.inject.annotations.Inject

interface AppPreferences {

    var apiConfiguration: ApiConfiguration

    fun observeApiConfiguration(): Flow<ApiConfiguration>
}

@OptIn(ExperimentalSettingsApi::class)
@Inject
class DefaultAppPreferences(
    internal val settings: ObservableSettings,
    dispatchers: AppCoroutineDispatchers,
) : AppPreferences {

    private val flowSettings by lazy { settings.toFlowSettings(dispatchers.io) }

    override var apiConfiguration: ApiConfiguration by ApiConfigurationDelegate()

    override fun observeApiConfiguration(): Flow<ApiConfiguration> {
        return combine(
            flowSettings.getStringOrNullFlow(HOST_URL_CONFIGURATION_KEY),
            flowSettings.getStringOrNullFlow(API_KEY_CONFIGURATION_KEY),
        ) { hostUrl: String?, apiKey: String? ->
            if (hostUrl == null || apiKey == null) {
                ApiConfiguration.NotSet
            } else {
                ApiConfiguration.Linkding(hostUrl, apiKey)
            }
        }
    }
}

private class ApiConfigurationDelegate :
    ReadWriteProperty<DefaultAppPreferences, ApiConfiguration> {
    override fun getValue(
        thisRef: DefaultAppPreferences,
        property: KProperty<*>,
    ): ApiConfiguration {
        val host = thisRef.settings.getStringOrNull(HOST_URL_CONFIGURATION_KEY)
        val apiKey = thisRef.settings.getStringOrNull(API_KEY_CONFIGURATION_KEY)
        return if (host == null || apiKey == null) {
            ApiConfiguration.NotSet
        } else {
            ApiConfiguration.Linkding(host, apiKey)
        }
    }

    override fun setValue(
        thisRef: DefaultAppPreferences,
        property: KProperty<*>,
        value: ApiConfiguration,
    ) {
        when (value) {
            is ApiConfiguration.Linkding -> {
                thisRef.settings.putString(HOST_URL_CONFIGURATION_KEY, value.hostUrl)
                thisRef.settings.putString(API_KEY_CONFIGURATION_KEY, value.hostUrl)
            }

            ApiConfiguration.NotSet -> {
                thisRef.settings.remove(HOST_URL_CONFIGURATION_KEY)
                thisRef.settings.remove(API_KEY_CONFIGURATION_KEY)
            }
        }
    }
}

private const val HOST_URL_CONFIGURATION_KEY = "hostUrl"
private const val API_KEY_CONFIGURATION_KEY = "apiKey"
