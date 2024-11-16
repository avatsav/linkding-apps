package dev.avatsav.linkding

import android.app.Application
import android.content.pm.ApplicationInfo
import dev.avatsav.linkding.initializers.AppInitializer
import dev.avatsav.linkding.prefs.AppPreferences
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.AppScope
import software.amazon.lastmile.kotlin.inject.anvil.MergeComponent
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

@SingleIn(AppScope::class)
@MergeComponent(AppScope::class)
abstract class AndroidAppComponent(@get:Provides val application: Application) {

    abstract val appPreferences: AppPreferences

    abstract val appInitializer: AppInitializer

    @SingleIn(AppScope::class)
    @Provides
    fun provideAppInfo(application: Application): AppInfo {
        val packageManager = application.packageManager
        val applicationInfo = packageManager.getApplicationInfo(application.packageName, 0)
        val packageInfo = packageManager.getPackageInfo(application.packageName, 0)

        return AppInfo(
            packageName = application.packageName,
            debug = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0,
            version = packageInfo.versionName!!,
        )
    }

    companion object
}
