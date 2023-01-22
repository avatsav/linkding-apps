package dev.avatsav.linkding.android

import android.app.Application
import dev.avatsav.linkding.inject.initKoin
import org.koin.android.ext.koin.androidContext
import timber.log.Timber

class LinkdingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        initKoin(enableNetworkLogs = BuildConfig.DEBUG) {
            androidContext(this@LinkdingApplication)
        }
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}
