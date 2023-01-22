package dev.avatsav.linkding.inject

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import dev.avatsav.linkding.Constants
import dev.avatsav.linkding.data.unfurl.LinkUnfurler
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults
import platform.LinkPresentation.LPMetadataProvider


actual fun platformModule() = module {
    single { flowSettings() }
    single { LinkUnfurler(LPMetadataProvider()) }
}

private fun flowSettings(): FlowSettings {
    val nsUserDefaults = NSUserDefaults(suiteName = Constants.SettingsKey)
    return NSUserDefaultsSettings(nsUserDefaults).toFlowSettings()
}
