package dev.avatsav.linkding.api.models

enum class LinkdingBookmarkFilter(val filterQuery: String) {
    None(""),
    Unread("!unread"),
    Untagged("!untagged")
}
