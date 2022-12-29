package dev.avatsav.linkding.ui.model

data class BookmarkViewItem(
    val id: Long,
    val title: String,
    val description: String,
    val urlHostName: String,
    val url: String,
    val tagNames: Set<String> = emptySet()
)