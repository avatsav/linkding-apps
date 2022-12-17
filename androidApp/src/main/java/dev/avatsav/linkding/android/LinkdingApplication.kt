package dev.avatsav.linkding.android

import android.app.Application
import dev.avatsav.linkding.inject.sharedModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class LinkdingApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@LinkdingApplication)
            modules(sharedModule(enableNetworkLogs = BuildConfig.DEBUG))
        }
    }

}