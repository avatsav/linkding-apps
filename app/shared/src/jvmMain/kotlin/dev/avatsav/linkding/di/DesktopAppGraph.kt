package dev.avatsav.linkding.di

import dev.avatsav.linkding.AppInfo
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import java.util.prefs.Preferences

@DependencyGraph(AppScope::class)
interface DesktopAppGraph : AppGraph {

  @SingleIn(AppScope::class)
  @Provides
  fun provideAppInfo(): AppInfo =
    AppInfo(packageName = "dev.avatsav.linkding", debug = true, version = "1.0.0")

  @SingleIn(AppScope::class)
  @Provides
  fun providePreferences(): Preferences = Preferences.userRoot().node("dev.avatsav.linkding")
}
