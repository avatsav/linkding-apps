package dev.avatsav.linkding.prefs

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.data.model.prefs.AppTheme
import dev.avatsav.linkding.prefs.HOST_URL_CONFIGURATION_KEY as HOST_URL_CONFIGURATION_KEY1
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

private const val HOST_URL_CONFIGURATION_KEY = "linkding-hostUrl"
private const val API_KEY_CONFIGURATION_KEY = "linkding-apiKey"
private const val USE_DYNAMIC_COLORS_KEY = "use-dynamic-colors"
private const val APP_THEME_KEY = "app-theme"

private const val APP_THEME_SYSTEM_VALUE = "system"
private const val APP_THEME_LIGHT_VALUE = "light"
private const val APP_THEME_DARK_VALUE = "dark"

@OptIn(ExperimentalSettingsApi::class)
@Inject
@SingleIn(AppScope::class)
@ContributesBinding(AppScope::class)
class DefaultAppPreferences(
  private val settings: ObservableSettings,
  dispatchers: AppCoroutineDispatchers,
) : AppPreferences {

  private val flowSettings by lazy { settings.toFlowSettings(dispatchers.io) }

  override fun observeApiConfig(): Flow<ApiConfig?> =
    combine(
      flowSettings.getStringOrNullFlow(HOST_URL_CONFIGURATION_KEY1),
      flowSettings.getStringOrNullFlow(API_KEY_CONFIGURATION_KEY),
    ) { hostUrl: String?, apiKey: String? ->
      if (hostUrl == null || apiKey == null) {
        null
      } else {
        ApiConfig(hostUrl, apiKey)
      }
    }

  override suspend fun setApiConfig(apiConfig: ApiConfig?) {
    if (apiConfig != null) {
      settings[HOST_URL_CONFIGURATION_KEY1] = apiConfig.hostUrl
      settings[API_KEY_CONFIGURATION_KEY] = apiConfig.apiKey
    } else {
      settings.remove(HOST_URL_CONFIGURATION_KEY1)
      settings.remove(API_KEY_CONFIGURATION_KEY)
    }
  }

  override fun getApiConfig(): ApiConfig? {
    val host: String? = settings[HOST_URL_CONFIGURATION_KEY1]
    val apiKey: String? = settings[API_KEY_CONFIGURATION_KEY]
    return if (host == null || apiKey == null) {
      null
    } else {
      ApiConfig(host, apiKey)
    }
  }

  override fun observeAppTheme(): Flow<AppTheme> =
    flowSettings.getStringFlow(APP_THEME_KEY, APP_THEME_SYSTEM_VALUE).map { it.appTheme }

  override suspend fun setAppTheme(appTheme: AppTheme) {
    settings[APP_THEME_KEY] = appTheme.storageValue
  }

  override fun observeUseDynamicColors(): Flow<Boolean> =
    flowSettings.getBooleanFlow(USE_DYNAMIC_COLORS_KEY, false)

  override suspend fun toggleUseDynamicColors() {
    settings[USE_DYNAMIC_COLORS_KEY] = !settings.getBoolean(USE_DYNAMIC_COLORS_KEY, false)
  }
}

private val AppTheme.storageValue: String
  get() =
    when (this) {
      AppTheme.System -> APP_THEME_SYSTEM_VALUE
      AppTheme.Light -> APP_THEME_LIGHT_VALUE
      AppTheme.Dark -> APP_THEME_DARK_VALUE
    }

private val String?.appTheme: AppTheme
  get() =
    when (this) {
      APP_THEME_LIGHT_VALUE -> AppTheme.Light
      APP_THEME_DARK_VALUE -> AppTheme.Dark
      else -> AppTheme.System
    }
