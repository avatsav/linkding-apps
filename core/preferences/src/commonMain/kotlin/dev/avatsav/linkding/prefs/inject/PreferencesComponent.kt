package dev.avatsav.linkding.prefs.inject

import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.prefs.AppPreferences
import dev.avatsav.linkding.prefs.DefaultAppPreferences
import me.tatarka.inject.annotations.Provides

expect interface PreferencesPlatformComponent

interface PreferencesComponent : PreferencesPlatformComponent {

    @AppScope
    val DefaultAppPreferences.bind: AppPreferences
        @Provides get() = this
}
