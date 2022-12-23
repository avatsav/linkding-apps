package dev.avatsav.linkding.inject

import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import dev.avatsav.linkding.Constants
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults


@OptIn(ExperimentalSettingsApi::class)
actual fun platformModule() = module {
    single { flowSettings() }
}

@OptIn(ExperimentalSettingsApi::class)
private fun flowSettings(): FlowSettings {
    val nsUserDefaults = NSUserDefaults(suiteName = Constants.SettingsKey)
    return NSUserDefaultsSettings(nsUserDefaults).toFlowSettings()
}

