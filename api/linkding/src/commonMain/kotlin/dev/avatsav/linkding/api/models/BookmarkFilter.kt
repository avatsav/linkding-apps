package dev.avatsav.linkding.api.models

enum class BookmarkFilter(val urlSuffix: String) {
    None("/"),
    Unread("/"),
    Untagged("/"),
    Archived("/archived/"),
}
