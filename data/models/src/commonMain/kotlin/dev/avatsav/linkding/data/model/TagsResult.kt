package dev.avatsav.linkding.data.model

data class TagsResult(
    val tags: List<Tag>,
    val previousPage: String?,
    val nextPage: String?,
)
