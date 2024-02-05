package dev.avatsav.linkding.data.model

data class SaveBookmark(
    val url: String,
    val title: String? = null,
    val description: String? = null,
    val tags: Set<String> = emptySet(),
    val archived: Boolean = false,
    val unread: Boolean = false,
    val shared: Boolean = false,
)
