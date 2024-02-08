package dev.avatsav.linkding.api

import io.ktor.client.engine.HttpClientEngineFactory
import io.ktor.client.engine.darwin.Darwin

internal actual val httpClientEngine: HttpClientEngineFactory<*> = Darwin
