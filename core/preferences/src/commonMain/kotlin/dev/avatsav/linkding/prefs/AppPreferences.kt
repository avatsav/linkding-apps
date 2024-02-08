package dev.avatsav.linkding.prefs

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.data.model.ApiConfig
import kotlinx.coroutines.flow.Flow

interface AppPreferences {

    var apiConfig: ApiConfig?

    fun observeApiConfig(): Flow<ApiConfig?>
}

@OptIn(ExperimentalSettingsApi::class)
internal class DefaultAppPreferences(
    internal val settings: ObservableSettings,
    dispatchers: AppCoroutineDispatchers,
) : AppPreferences {

    private val flowSettings by lazy { settings.toFlowSettings(dispatchers.io) }

    override var apiConfig: ApiConfig? by ApiConfigDelegate()

    override fun observeApiConfig(): Flow<ApiConfig?> = flowSettings.observeApiConfig()
}
