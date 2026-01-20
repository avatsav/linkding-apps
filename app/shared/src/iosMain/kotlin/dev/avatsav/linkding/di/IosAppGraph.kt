package dev.avatsav.linkding.di

import dev.avatsav.linkding.AppInfo
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import kotlin.experimental.ExperimentalNativeApi
import platform.Foundation.NSBundle
import platform.Foundation.NSUserDefaults

@OptIn(ExperimentalNativeApi::class)
@DependencyGraph(AppScope::class)
interface IosAppGraph : AppGraph {

  @SingleIn(AppScope::class)
  @Provides
  fun provideAppInfo(): AppInfo =
    AppInfo(
      packageName = NSBundle.mainBundle.bundleIdentifier ?: error("Bundle ID not found"),
      debug = Platform.isDebugBinary,
      version =
        NSBundle.mainBundle.infoDictionary?.get("CFBundleShortVersionString") as? String ?: "",
    )

  @SingleIn(AppScope::class)
  @Provides
  fun provideNsUserDefaults(): NSUserDefaults = NSUserDefaults.standardUserDefaults
}
