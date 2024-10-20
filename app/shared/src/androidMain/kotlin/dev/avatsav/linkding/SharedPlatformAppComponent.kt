package dev.avatsav.linkding

import android.app.Application
import android.content.pm.ApplicationInfo
import dev.avatsav.linkding.inject.AppScope
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

actual interface SharedPlatformAppComponent {

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
}
