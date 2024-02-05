package dev.avatsav.linkding.prefs

import com.russhwolf.settings.ObservableSettings
import com.russhwolf.settings.PreferencesSettings
import dev.avatsav.linkding.inject.AppScope
import java.util.prefs.Preferences
import me.tatarka.inject.annotations.Provides

actual interface PreferencesPlatformComponent {
    @AppScope
    @Provides
    fun provideSettings(delegate: Preferences): ObservableSettings = PreferencesSettings(delegate)
}
