package dev.avatsav.linkding.data.model

data class BookmarksResult(
    val bookmarks: List<Bookmark>,
    val previousPage: String?,
    val nextPage: String?,
)
