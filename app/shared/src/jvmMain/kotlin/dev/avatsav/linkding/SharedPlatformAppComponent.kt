package dev.avatsav.linkding

import dev.avatsav.linkding.inject.AppScope
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn
import java.util.prefs.Preferences

actual interface SharedPlatformAppComponent {

    @SingleIn(AppScope::class)
    @Provides
    fun provideAppInfo(): AppInfo = AppInfo(
        packageName = "dev.avatsav.linkding",
        debug = true,
        version = "1.0.0",
    )

    @SingleIn(AppScope::class)
    @Provides
    fun providePreferences(): Preferences = Preferences.userRoot().node("dev.avatsav.linkding")
}
