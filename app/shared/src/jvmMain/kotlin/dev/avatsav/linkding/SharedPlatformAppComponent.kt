package dev.avatsav.linkding

import dev.avatsav.linkding.inject.AppScope
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp
import me.tatarka.inject.annotations.Provides
import java.util.prefs.Preferences

actual interface SharedPlatformAppComponent {

    @AppScope
    @Provides
    fun provideAppInfo(): AppInfo = AppInfo(
        packageName = "dev.avatsav.linkding",
        debug = true,
        version = "1.0.0",
    )

    @AppScope
    @Provides
    fun provideHttpClientEngineFactory(): HttpClientEngineFactory<*> = OkHttp

    @AppScope
    @Provides
    fun providePreferences(): Preferences = Preferences.userRoot().node("dev.avatsav.linkding")
}
