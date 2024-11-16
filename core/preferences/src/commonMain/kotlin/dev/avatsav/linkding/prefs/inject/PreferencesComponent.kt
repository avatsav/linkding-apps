package dev.avatsav.linkding.prefs.inject

import dev.avatsav.linkding.inject.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.ContributesTo

expect interface PreferencesPlatformComponent

@ContributesTo(AppScope::class)
interface PreferencesComponent : PreferencesPlatformComponent
