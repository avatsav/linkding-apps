package dev.avatsav.linkding.data.network.inject

import dev.avatsav.linkding.inject.AppScope
import dev.avatsav.linkding.inject.annotations.SingleIn
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp
import me.tatarka.inject.annotations.Provides

actual interface PlatformNetworkComponent {

    @SingleIn(AppScope::class)
    @Provides
    fun provideHttpClientEngineFactory(): HttpClientEngineFactory<*> = OkHttp
}
