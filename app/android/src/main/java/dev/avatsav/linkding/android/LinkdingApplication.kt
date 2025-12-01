package dev.avatsav.linkding.android

import android.app.Application
import dev.avatsav.linkding.android.di.AndroidAppGraph
import dev.avatsav.linkding.di.GraphHolder
import dev.zacsweers.metro.createGraphFactory
import dev.zacsweers.metrox.android.MetroAppComponentProviders
import dev.zacsweers.metrox.android.MetroApplication

class LinkdingApplication : Application(), MetroApplication {

  val appGraph: AndroidAppGraph by lazy {
    createGraphFactory<AndroidAppGraph.Factory>().create(this)
  }

  override val appComponentProviders: MetroAppComponentProviders
    get() = appGraph

  override fun onCreate() {
    super.onCreate()
    appGraph.appInitializer.initialize()
    GraphHolder.components += appGraph
  }
}
