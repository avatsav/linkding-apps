package dev.avatsav.linkding.api

import com.github.michaelbull.result.Result
import dev.avatsav.linkding.api.models.LinkdingError
import dev.avatsav.linkding.api.models.LinkdingTagsResponse

interface LinkdingTagsApi {
  suspend fun getTags(
    offset: Int,
    limit: Int,
    query: String = "",
  ): Result<LinkdingTagsResponse, LinkdingError>
}
