package dev.avatsav.linkding.data.network.inject

import dev.avatsav.linkding.inject.AppScope
import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin
import me.tatarka.inject.annotations.Provides
import software.amazon.lastmile.kotlin.inject.anvil.SingleIn

actual interface PlatformNetworkComponent {

    @SingleIn(AppScope::class)
    @Provides
    fun provideHttpClientEngineFactory(): HttpClientEngineFactory<*> = Darwin
}
