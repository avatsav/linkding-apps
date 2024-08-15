package dev.avatsav.linkding.data.bookmarks

import io.ktor.client.engine.HttpClientEngineFactory

internal expect val httpClientEngine: HttpClientEngineFactory<*>
