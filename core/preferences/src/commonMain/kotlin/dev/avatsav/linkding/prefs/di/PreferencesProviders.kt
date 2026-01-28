package dev.avatsav.linkding.prefs.di

import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesTo

expect interface PreferencesPlatformProviders

@ContributesTo(AppScope::class) interface PreferencesProviders : PreferencesPlatformProviders
