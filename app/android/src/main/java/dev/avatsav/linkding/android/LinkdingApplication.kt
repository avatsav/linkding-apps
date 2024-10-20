package dev.avatsav.linkding.android

import android.app.Application
import dev.avatsav.linkding.AndroidAppComponent

class LinkdingApplication : Application() {

    lateinit var component: AndroidAppComponent

    override fun onCreate() {
        super.onCreate()
        component = AndroidAppComponent::class.create(this)
    }
}
