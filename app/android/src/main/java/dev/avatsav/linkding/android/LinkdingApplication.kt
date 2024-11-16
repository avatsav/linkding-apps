package dev.avatsav.linkding.android

import android.app.Application
import dev.avatsav.linkding.AndroidAppComponent
import dev.avatsav.linkding.create
import dev.avatsav.linkding.inject.ComponentHolder

class LinkdingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ComponentHolder.components += AndroidAppComponent::class.create(this)
            .also { it.appInitializer.initialize() }
    }
}
