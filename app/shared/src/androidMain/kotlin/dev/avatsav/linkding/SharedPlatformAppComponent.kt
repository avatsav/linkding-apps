package dev.avatsav.linkding

import android.app.Application
import android.content.pm.ApplicationInfo
import dev.avatsav.linkding.inject.AppScope
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp
import me.tatarka.inject.annotations.Provides

actual interface SharedPlatformAppComponent {

    @AppScope
    @Provides
    fun provideAppInfo(application: Application): AppInfo {
        val packageManager = application.packageManager
        val applicationInfo = packageManager.getApplicationInfo(application.packageName, 0)
        val packageInfo = packageManager.getPackageInfo(application.packageName, 0)

        return AppInfo(
            packageName = application.packageName,
            debug = (applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE) != 0,
            version = packageInfo.versionName,
        )
    }

    @AppScope
    @Provides
    fun provideHttpClientEngineFactory(): HttpClientEngineFactory<*> = OkHttp
}
