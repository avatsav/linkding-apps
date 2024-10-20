package dev.avatsav.linkding.android

import android.app.Application
import dev.avatsav.linkding.AndroidAppComponent
import dev.avatsav.linkding.inject.ComponentHolder
import kimchi.merge.dev.avatsav.linkding.createAndroidAppComponent

class LinkdingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        ComponentHolder.components += AndroidAppComponent.createAndroidAppComponent(this)
    }
}
