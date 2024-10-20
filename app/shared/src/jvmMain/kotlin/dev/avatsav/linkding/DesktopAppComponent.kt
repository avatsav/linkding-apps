package dev.avatsav.linkding

import com.r0adkll.kimchi.annotations.MergeComponent
import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import me.tatarka.inject.annotations.Provides
import java.util.prefs.Preferences

@SingleIn(AppScope::class)
@MergeComponent(AppScope::class)
abstract class DesktopAppComponent {

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

    companion object
}
