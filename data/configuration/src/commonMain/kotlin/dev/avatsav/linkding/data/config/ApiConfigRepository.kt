package dev.avatsav.linkding.data.config

import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.prefs.AppPreferences
import me.tatarka.inject.annotations.Inject

@Inject
class ApiConfigRepository(
    private val appPreferences: AppPreferences,
) {

    var apiConfig: ApiConfig? = appPreferences.apiConfig

    fun observeApiConfiguration() = appPreferences.observeApiConfig()
}
