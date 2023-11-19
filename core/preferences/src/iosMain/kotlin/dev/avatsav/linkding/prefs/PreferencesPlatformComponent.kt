package dev.avatsav.linkding.prefs

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import me.tatarka.inject.annotations.Provides
import platform.Foundation.NSUserDefaults

actual interface PreferencesPlatformComponent {
    @Provides
    fun provideSettings(delegate: NSUserDefaults): ObservableSettings =
        NSUserDefaultsSettings(delegate)
}
