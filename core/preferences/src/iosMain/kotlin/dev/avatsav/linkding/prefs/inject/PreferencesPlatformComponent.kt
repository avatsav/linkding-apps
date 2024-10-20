package dev.avatsav.linkding.prefs.inject

import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.ObservableSettings
import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import me.tatarka.inject.annotations.Provides
import platform.Foundation.NSUserDefaults

actual interface PreferencesPlatformComponent {
    @Provides
    @SingleIn(AppScope::class)
    fun provideSettings(delegate: NSUserDefaults): ObservableSettings =
        NSUserDefaultsSettings(delegate)
}
