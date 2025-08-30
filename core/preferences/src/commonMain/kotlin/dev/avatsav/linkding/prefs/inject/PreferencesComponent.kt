package dev.avatsav.linkding.prefs.inject

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo

expect interface PreferencesPlatformComponent

@ContributesTo(AppScope::class)
interface PreferencesComponent : PreferencesPlatformComponent
