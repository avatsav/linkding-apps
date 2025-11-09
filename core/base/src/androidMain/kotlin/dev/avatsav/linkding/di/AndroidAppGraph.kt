package dev.avatsav.linkding.di

import android.app.Activity
import dev.zacsweers.metro.Multibinds
import dev.zacsweers.metro.Provider
import kotlin.reflect.KClass

actual interface PlatformAppGraph {
  /**
   * A multibinding map of activity classes to their providers accessible for
   * [MetroAppComponentFactory].
   */
  @Multibinds(allowEmpty = true)
  val activityProviders: Map<KClass<out Activity>, Provider<Activity>>
}
