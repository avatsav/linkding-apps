package dev.avatsav.linkding

import android.app.Application
import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.prefs.AppPreferences
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.Provides

@Component
@AppScope
abstract class AndroidAppComponent(
    @get:Provides val application: Application,
) : SharedAppComponent {
    abstract val appPreferences: AppPreferences

    companion object
}
