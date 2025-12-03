package dev.avatsav.linkding.android.di

import android.app.Application
import dev.avatsav.linkding.di.SharedAndroidAppGraph
import dev.avatsav.linkding.prefs.AppPreferences
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Provides
import dev.zacsweers.metrox.android.MetroAppComponentProviders

@DependencyGraph(AppScope::class)
interface AndroidAppGraph : SharedAndroidAppGraph, MetroAppComponentProviders {

  val appPreferences: AppPreferences

  @DependencyGraph.Factory
  interface Factory {
    fun create(@Provides application: Application): AndroidAppGraph
  }
}
