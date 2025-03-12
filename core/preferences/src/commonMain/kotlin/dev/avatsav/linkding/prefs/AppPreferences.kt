package dev.avatsav.linkding.prefs

import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.data.model.prefs.AppTheme
import kotlinx.coroutines.flow.Flow

interface AppPreferences {
  fun observeApiConfig(): Flow<ApiConfig?>

  suspend fun setApiConfig(apiConfig: ApiConfig?)

  fun getApiConfig(): ApiConfig?

  fun observeAppTheme(): Flow<AppTheme>

  suspend fun setAppTheme(appTheme: AppTheme)

  fun observeUseDynamicColors(): Flow<Boolean>

  suspend fun toggleUseDynamicColors()
}
