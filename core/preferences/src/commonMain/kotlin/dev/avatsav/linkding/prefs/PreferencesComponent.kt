package dev.avatsav.linkding.prefs

import dev.avatsav.linkding.inject.AppScope
import me.tatarka.inject.annotations.Provides

expect interface PreferencesPlatformComponent

const val AppPreferencesStorageKey = "app-preferences"

interface PreferencesComponent : PreferencesPlatformComponent {

    @AppScope
    @Provides
    fun appPreferences(fromPlatform: DefaultAppPreferences): AppPreferences = fromPlatform
}
