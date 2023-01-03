package dev.avatsav.linkding.bookmark.domain

enum class BookmarkFilter(val urlSuffix: String) {
    None("/"),
    Archived("/archived"),
    Unread("/unread")

}