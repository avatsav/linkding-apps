package dev.avatsav.linkding

import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.prefs.PreferencesPlatformComponent
import me.tatarka.inject.annotations.Provides

interface LoggerComponent : PreferencesPlatformComponent {

    @AppScope
    @Provides
    fun providePreferences(impl: KermitLogger): Logger = impl
}
