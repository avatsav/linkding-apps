package dev.avatsav.linkding.prefs

import dev.avatsav.linkding.inject.AppScope
import me.tatarka.inject.annotations.Provides

expect interface PreferencesPlatformComponent

interface PreferencesComponent : PreferencesPlatformComponent {

    @AppScope
    @Provides
    fun providePreferences(impl: DefaultAppPreferences): AppPreferences = impl
}
