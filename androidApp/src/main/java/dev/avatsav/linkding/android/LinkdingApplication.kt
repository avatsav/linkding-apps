package dev.avatsav.linkding.android

import android.app.Application
import dev.avatsav.linkding.inject.initKoin
import org.koin.android.ext.koin.androidContext

class LinkdingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(enableNetworkLogs = BuildConfig.DEBUG) {
            androidContext(this@LinkdingApplication)
        }
    }

}