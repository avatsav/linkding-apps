package dev.avatsav.linkding.api.extensions

import dev.avatsav.linkding.api.models.LinkdingBookmarkCategory
import dev.avatsav.linkding.api.models.LinkdingBookmarkCategory.Unread
import dev.avatsav.linkding.api.models.LinkdingBookmarkCategory.Untagged
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.parameter
import io.ktor.http.path

internal fun HttpRequestBuilder.endpointTags(vararg paths: String) {
  url { path(buildPaths("api", "tags", *paths)) }
}

internal fun HttpRequestBuilder.endpointBookmarks(vararg paths: String) {
  url { path(buildPaths("api", "bookmarks", *paths)) }
}

internal fun HttpRequestBuilder.parameterPage(offset: Int, limit: Int) {
  parameter("offset", offset.toString())
  parameter("limit", limit.toString())
}

internal fun HttpRequestBuilder.parameterQuery(query: String) {
  parameter("q", query)
}

internal fun HttpRequestBuilder.parameterQuery(
  query: String,
  category: LinkdingBookmarkCategory,
  tags: List<String>,
) {
  val constructedQuery =
    buildString {
        append("$query ")
        append(tags.joinToString(separator = " ") { "#$it" })
        when (category) {
          Unread -> append("!unread ")
          Untagged -> append("!untagged ")
          else -> {}
        }
      }
      .trim()
  if (constructedQuery.isNotBlank()) {
    parameter("q", constructedQuery)
  }
}

/** Post-fixing "/" because linkding api requires it to be present. It's a python + django quirk. */
private fun buildPaths(vararg paths: String): String =
  paths.joinToString(separator = "/", postfix = "/")
