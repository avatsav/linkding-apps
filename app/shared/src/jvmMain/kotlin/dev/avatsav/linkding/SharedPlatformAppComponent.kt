package dev.avatsav.linkding

import dev.avatsav.linkding.inject.AppScope
import me.tatarka.inject.annotations.Provides
import java.util.prefs.Preferences

actual interface SharedPlatformAppComponent {

    @AppScope
    @Provides
    fun provideAppInfo(): AppInfo = AppInfo(
        packageName = "dev.avatsav.linkding",
        debug = true,
        version = "1.0.0",
    )

    @AppScope
    @Provides
    fun providePreferences(): Preferences = Preferences.userRoot().node("dev.avatsav.linkding")
}
