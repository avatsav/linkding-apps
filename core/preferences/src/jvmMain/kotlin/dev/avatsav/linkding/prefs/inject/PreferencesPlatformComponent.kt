package dev.avatsav.linkding.prefs.inject

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import dev.avatsav.linkding.inject.AppScope
import me.tatarka.inject.annotations.Provides
import java.util.prefs.Preferences

actual interface PreferencesPlatformComponent {
    @AppScope
    @Provides
    fun provideSettings(delegate: Preferences): ObservableSettings = PreferencesSettings(delegate)
}
