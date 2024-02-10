package dev.avatsav.linkding.data.config

import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.prefs.AppPreferences
import me.tatarka.inject.annotations.Inject

@Inject
class ApiConfigRepository(
    private val appPreferences: AppPreferences,
) {

    var apiConfig: ApiConfig?
        get() = appPreferences.apiConfig
        set(value) {
            appPreferences.apiConfig = value
        }

    fun observeApiConfiguration() = appPreferences.observeApiConfig()
}
