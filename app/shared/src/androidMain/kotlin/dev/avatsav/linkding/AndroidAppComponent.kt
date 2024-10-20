package dev.avatsav.linkding

import android.app.Application
import android.content.pm.ApplicationInfo
import com.r0adkll.kimchi.annotations.MergeComponent
import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import dev.avatsav.linkding.prefs.AppPreferences
import me.tatarka.inject.annotations.Provides

@SingleIn(AppScope::class)
@MergeComponent(AppScope::class)
abstract class AndroidAppComponent(@get:Provides val application: Application) {

    abstract val appPreferences: AppPreferences

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
