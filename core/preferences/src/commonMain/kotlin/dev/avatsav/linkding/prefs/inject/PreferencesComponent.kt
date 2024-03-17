package dev.avatsav.linkding.prefs.inject

import com.russhwolf.settings.ObservableSettings
import dev.avatsav.linkding.AppCoroutineDispatchers
import dev.avatsav.linkding.Logger
import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.prefs.AppPreferences
import dev.avatsav.linkding.prefs.DefaultAppPreferences
import me.tatarka.inject.annotations.Provides

expect interface PreferencesPlatformComponent

interface PreferencesComponent : PreferencesPlatformComponent {
    @AppScope
    @Provides
    fun providePreferences(
        settings: ObservableSettings,
        logger: Logger,
        dispatchers: AppCoroutineDispatchers,
    ): AppPreferences {
        return DefaultAppPreferences(settings, logger, dispatchers)
    }
}
