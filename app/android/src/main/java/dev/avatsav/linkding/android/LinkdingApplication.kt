package dev.avatsav.linkding.android

import android.app.Application
import dev.avatsav.linkding.AndroidAppComponent
import dev.avatsav.linkding.create

class LinkdingApplication : Application() {

    val component: AndroidAppComponent by lazy {
        AndroidAppComponent.create(this)
    }
}
