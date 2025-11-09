package dev.avatsav.linkding.di.activity

import android.app.Activity
import android.app.Application
import android.content.Intent
import androidx.annotation.Keep
import androidx.core.app.AppComponentFactory
import dev.avatsav.linkding.LinkdingApp
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.Provider
import kotlin.reflect.KClass

/**
 * An [AppComponentFactory] that uses Metro for constructor injection of Activities.
 *
 * If you have minSdk < 28, you can fall back to using member injection on Activities or (better)
 * use an architecture that abstracts the Android framework components away.
 */
@Keep
@ContributesBinding(AppScope::class)
@Inject
class MetroAppComponentFactory : AppComponentFactory() {

  private inline fun <reified T : Any> getInstance(
    cl: ClassLoader,
    className: String,
    providers: Map<KClass<out T>, Provider<T>>,
  ): T? {
    val clazz = Class.forName(className, false, cl).asSubclass(T::class.java)
    val modelProvider = providers[clazz.kotlin] ?: return null
    return modelProvider()
  }

  override fun instantiateActivityCompat(
    cl: ClassLoader,
    className: String,
    intent: Intent?,
  ): Activity {
    return getInstance(cl, className, activityProviders)
      ?: super.instantiateActivityCompat(cl, className, intent)
  }

  override fun instantiateApplicationCompat(cl: ClassLoader, className: String): Application {
    val app = super.instantiateApplicationCompat(cl, className)
    activityProviders = (app as LinkdingApp).appGraph.activityProviders
    return app
  }

  // AppComponentFactory can be created multiple times
  companion object {
    private lateinit var activityProviders: Map<KClass<out Activity>, Provider<Activity>>
  }
}
