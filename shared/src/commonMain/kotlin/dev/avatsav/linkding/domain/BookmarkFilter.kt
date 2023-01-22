package dev.avatsav.linkding.domain

enum class BookmarkFilter(val urlSuffix: String) {
    None("/"),
    Unread("/"),
    Untagged("/"),
    Archived("/archived/"),
}
