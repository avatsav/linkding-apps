package dev.avatsav.linkding.bookmark.domain

enum class BookmarkFilter(val urlSuffix: String) {
    None("/"),
    Unread("/"),
    Untagged("/"),
    Archived("/archived/"),
}