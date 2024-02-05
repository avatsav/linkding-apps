package dev.avatsav.linkding.prefs.inject

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import dev.avatsav.linkding.inject.AppScope
import me.tatarka.inject.annotations.Provides
import platform.Foundation.NSUserDefaults

actual interface PreferencesPlatformComponent {
    @Provides
    @AppScope
    fun provideSettings(delegate: NSUserDefaults): ObservableSettings =
        NSUserDefaultsSettings(delegate)
}
