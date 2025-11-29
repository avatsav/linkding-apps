package dev.avatsav.linkding.android.di

import android.app.Activity
import android.app.Application
import dev.avatsav.linkding.di.SharedAndroidAppGraph
import dev.avatsav.linkding.di.viewmodel.ViewModelGraph
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.DependencyGraph
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provider
import dev.zacsweers.metro.Provides
import kotlin.reflect.KClass

@DependencyGraph(AppScope::class)
interface AndroidAppGraph : SharedAndroidAppGraph, ViewModelGraph {

  /**
   * A multibinding map of activity classes to their providers accessible for
   * [MetroAppComponentFactory].
   */
  @Multibinds val activityProviders: Map<KClass<out Activity>, Provider<Activity>>

  @DependencyGraph.Factory
  interface Factory {
    fun create(@Provides application: Application): AndroidAppGraph
  }
}
