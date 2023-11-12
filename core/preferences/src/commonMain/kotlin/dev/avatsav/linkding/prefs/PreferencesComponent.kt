package dev.avatsav.linkding.prefs

import dev.avatsav.linkding.inject.ApplicationScope
import me.tatarka.inject.annotations.Provides

expect interface PreferencesPlatformComponent

const val AppPreferencesStorageKey = "app-preferences"

interface PreferencesComponent : PreferencesPlatformComponent {

    @ApplicationScope
    @Provides
    fun appPreferences(fromPlatform: DefaultAppPreferences): AppPreferences = fromPlatform


}
