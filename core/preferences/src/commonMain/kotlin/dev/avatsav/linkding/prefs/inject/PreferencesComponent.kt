package dev.avatsav.linkding.prefs.inject

import com.r0adkll.kimchi.annotations.ContributesTo
import dev.avatsav.linkding.inject.AppScope

expect interface PreferencesPlatformComponent

@ContributesTo(AppScope::class)
interface PreferencesComponent : PreferencesPlatformComponent
