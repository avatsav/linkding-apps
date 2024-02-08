package dev.avatsav.linkding.api

import io.ktor.client.engine.HttpClientEngineFactory

internal expect val httpClientEngine: HttpClientEngineFactory<*>
