package dev.avatsav.linkding.inject

import android.app.Application
import android.content.pm.ApplicationInfo
import dev.avatsav.linkding.AppInfo
import dev.avatsav.linkding.initializers.AppInitializer
import dev.avatsav.linkding.prefs.AppPreferences
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn

@DependencyGraph(AppScope::class)
abstract class AndroidAppComponent {

  abstract val appPreferences: AppPreferences

  abstract val appInitializer: AppInitializer

  @Suppress("UnsafeCallOnNullableType")
  @SingleIn(AppScope::class)
  @Provides
  fun provideAppInfo(application: Application): AppInfo {
    val packageManager = application.packageManager
    val applicationInfo = packageManager.getApplicationInfo(application.packageName, 0)
    val packageInfo = packageManager.getPackageInfo(application.packageName, 0)

    return AppInfo(
      packageName = application.packageName,
      debug = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0,
      version = packageInfo.versionName!!,
    )
  }

  @DependencyGraph.Factory
  interface Factory {
    fun create(@Provides application: Application): AndroidAppComponent
  }
}
