package dev.avatsav.linkding.data.network.inject

import dev.avatsav.linkding.inject.AppScope
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp
import me.tatarka.inject.annotations.Provides

actual interface PlatformNetworkComponent {

    @AppScope
    @Provides
    fun provideHttpClientEngineFactory(): HttpClientEngineFactory<*> = OkHttp
}
