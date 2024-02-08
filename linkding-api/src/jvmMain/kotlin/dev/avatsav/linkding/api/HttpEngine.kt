package dev.avatsav.linkding.api

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.okhttp.OkHttp

internal actual val httpClientEngine: HttpClientEngineFactory<*> = OkHttp
