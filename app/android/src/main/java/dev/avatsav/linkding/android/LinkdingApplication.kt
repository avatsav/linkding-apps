package dev.avatsav.linkding.android

import android.app.Application
import dev.avatsav.linkding.android.di.AndroidAppGraph
import dev.avatsav.linkding.di.ComponentHolder
import dev.zacsweers.metro.createGraphFactory

class LinkdingApplication : Application() {

  val appGraph: AndroidAppGraph by lazy {
    createGraphFactory<AndroidAppGraph.Factory>().create(this)
  }

  override fun onCreate() {
    super.onCreate()
    appGraph.appInitializer.initialize()
    ComponentHolder.components += appGraph
  }
}
