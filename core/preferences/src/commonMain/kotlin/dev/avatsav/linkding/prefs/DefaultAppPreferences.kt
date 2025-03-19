package dev.avatsav.linkding.prefs

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.get
import com.russhwolf.settings.set
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.data.model.ApiConfig
import dev.avatsav.linkding.data.model.prefs.AppTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesBinding
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

private const val HostUrlConfigurationKey = "linkding-hostUrl"
private const val ApiKeyConfigurationKey = "linkding-apiKey"
private const val UseDynamicColorsKey = "use-dynamic-colors"
private const val AppThemeKey = "app-theme"

private const val SystemThemeValue = "system"
private const val LightThemeValue = "light"
private const val DarkThemeValue = "dark"

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
      flowSettings.getStringOrNullFlow(HostUrlConfigurationKey),
      flowSettings.getStringOrNullFlow(ApiKeyConfigurationKey),
    ) { hostUrl: String?, apiKey: String? ->
      if (hostUrl == null || apiKey == null) {
        null
      } else {
        ApiConfig(hostUrl, apiKey)
      }
    }

  override suspend fun setApiConfig(apiConfig: ApiConfig?) {
    if (apiConfig != null) {
      settings[HostUrlConfigurationKey] = apiConfig.hostUrl
      settings[ApiKeyConfigurationKey] = apiConfig.apiKey
    } else {
      settings.remove(HostUrlConfigurationKey)
      settings.remove(ApiKeyConfigurationKey)
    }
  }

  override fun getApiConfig(): ApiConfig? {
    val host: String? = settings[HostUrlConfigurationKey]
    val apiKey: String? = settings[ApiKeyConfigurationKey]
    return if (host == null || apiKey == null) {
      null
    } else {
      ApiConfig(host, apiKey)
    }
  }

  override fun observeAppTheme(): Flow<AppTheme> =
    flowSettings.getStringFlow(AppThemeKey, SystemThemeValue).map { it.appTheme }

  override suspend fun setAppTheme(appTheme: AppTheme) {
    settings[AppThemeKey] = appTheme.storageValue
  }

  override fun observeUseDynamicColors(): Flow<Boolean> =
    flowSettings.getBooleanFlow(UseDynamicColorsKey, false)

  override suspend fun toggleUseDynamicColors() {
    settings[UseDynamicColorsKey] = !settings.getBoolean(UseDynamicColorsKey, false)
  }
}

private val AppTheme.storageValue: String
  get() =
    when (this) {
      AppTheme.System -> SystemThemeValue
      AppTheme.Light -> LightThemeValue
      AppTheme.Dark -> DarkThemeValue
    }

private val String?.appTheme: AppTheme
  get() =
    when (this) {
      LightThemeValue -> AppTheme.Light
      DarkThemeValue -> AppTheme.Dark
      else -> AppTheme.System
    }
