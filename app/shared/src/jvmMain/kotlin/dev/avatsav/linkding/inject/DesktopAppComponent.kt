package dev.avatsav.linkding.inject

import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.initializers.AppInitializer
import java.util.prefs.Preferences
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@SingleIn(AppScope::class)
@MergeComponent(AppScope::class)
interface DesktopAppComponent {

  val appInitializer: AppInitializer

  @SingleIn(AppScope::class)
  @Provides
  fun provideAppInfo(): AppInfo =
    AppInfo(packageName = "dev.avatsav.linkding", debug = true, version = "1.0.0")

  @SingleIn(AppScope::class)
  @Provides
  fun providePreferences(): Preferences = Preferences.userRoot().node("dev.avatsav.linkding")

  companion object
}
