package dev.avatsav.linkding.settings.impl

import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.prefs.AppPreferences
import dev.avatsav.linkding.settings.api.SettingsManager
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class SettingsManagerImpl(private val preferences: AppPreferences) : SettingsManager {

  override fun observeApiConfig(): Flow<ApiConfig?> = preferences.observeApiConfig()

  override fun getApiConfig(): ApiConfig? = preferences.getApiConfig()

  override suspend fun setApiConfig(apiConfig: ApiConfig?) = preferences.setApiConfig(apiConfig)

  override fun observeAppTheme(): Flow<AppTheme> = preferences.observeAppTheme()

  override suspend fun setAppTheme(theme: AppTheme) = preferences.setAppTheme(theme)

  override fun observeUseDynamicColors(): Flow<Boolean> = preferences.observeUseDynamicColors()

  override suspend fun toggleUseDynamicColors() = preferences.toggleUseDynamicColors()
}
