package dev.avatsav.linkding.api

import dev.avatsav.linkding.api.impl.DefaultLinkdingBookmarksApi
import dev.avatsav.linkding.api.impl.DefaultLinkdingTagsApi
import io.ktor.client.HttpClient

@LinkdingDsl
fun Linkding(apiConfig: LinkdingApiConfig, block: LinkdingClientConfig.() -> Unit): Linkding {
  val clientConfig = LinkdingClientConfig().apply(block)
  return DefaultLinkding(apiConfig, clientConfig)
}

interface Linkding {
  val bookmarks: LinkdingBookmarksApi
  val tags: LinkdingTagsApi
}

internal class DefaultLinkding(apiConfig: LinkdingApiConfig, clientConfig: LinkdingClientConfig) :
  Linkding {

  private val httpClient: HttpClient = HttpClientFactory.buildHttpClient(clientConfig, apiConfig)

  override val bookmarks: LinkdingBookmarksApi by buildApi(::DefaultLinkdingBookmarksApi)
  override val tags: LinkdingTagsApi by buildApi(::DefaultLinkdingTagsApi)

  private inline fun <T> buildApi(crossinline builder: (HttpClient) -> T) = lazy {
    builder(httpClient)
  }
}
