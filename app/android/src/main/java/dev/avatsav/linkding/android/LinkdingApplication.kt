package dev.avatsav.linkding.android

import android.app.Application
import dev.avatsav.linkding.inject.AndroidAppComponent
import dev.avatsav.linkding.inject.ComponentHolder
import dev.avatsav.linkding.inject.create

class LinkdingApplication : Application() {

  override fun onCreate() {
    super.onCreate()
    ComponentHolder.components +=
      AndroidAppComponent::class.create(this).also { it.appInitializer.initialize() }
  }
}
