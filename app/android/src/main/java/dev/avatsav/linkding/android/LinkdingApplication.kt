package dev.avatsav.linkding.android

import android.app.Application
import dev.avatsav.linkding.inject.AndroidAppComponent
import dev.avatsav.linkding.inject.ComponentHolder
import dev.zacsweers.metro.createGraphFactory

class LinkdingApplication : Application() {

  val appGraph: AndroidAppComponent by lazy {
    createGraphFactory<AndroidAppComponent.Factory>().create(this).also {
      it.appInitializer.initialize()
    }
  }

  override fun onCreate() {
    super.onCreate()
    ComponentHolder.components += appGraph
  }
}
