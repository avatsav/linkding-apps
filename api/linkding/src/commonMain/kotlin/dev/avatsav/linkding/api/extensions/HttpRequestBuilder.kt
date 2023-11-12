package dev.avatsav.linkding.api.extensions

import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.parameter
import io.ktor.http.path
import io.ktor.http.takeFrom

internal fun HttpRequestBuilder.endpointTags(vararg paths: String) {
    url {
        path("api/tags/")
    }
}

internal fun HttpRequestBuilder.endpointBookmarks(host: String, vararg paths: String) {
    url {
        takeFrom(host)
        path("api/bookmarks/")
    }
}

internal fun HttpRequestBuilder.parameterPage(offset: Int, limit: Int) {
    parameter("offset", offset.toString())
    parameter("limit", limit.toString())
}

internal fun HttpRequestBuilder.parameterQuery(query: String) {
    parameter("q", query)
}

