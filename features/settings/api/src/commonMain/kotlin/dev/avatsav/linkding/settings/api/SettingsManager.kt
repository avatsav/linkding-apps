package dev.avatsav.linkding.settings.api

import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.data.model.prefs.AppTheme
import kotlinx.coroutines.flow.Flow

/** Settings management interface for handling app preferences and configuration. */
interface SettingsManager {

  /** Observe the current API configuration. */
  fun observeApiConfig(): Flow<ApiConfig?>

  /** Get the current API configuration. */
  fun getApiConfig(): ApiConfig?

  /** Set the API configuration. */
  suspend fun setApiConfig(apiConfig: ApiConfig?)

  /** Observe the current app theme. */
  fun observeAppTheme(): Flow<AppTheme>

  /** Set the app theme. */
  suspend fun setAppTheme(theme: AppTheme)

  /** Observe the dynamic colors setting. */
  fun observeUseDynamicColors(): Flow<Boolean>

  /** Toggle the use of dynamic colors. */
  suspend fun toggleUseDynamicColors()
}
