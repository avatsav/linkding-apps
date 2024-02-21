package dev.avatsav.linkding.api.models

enum class LinkdingBookmarkCategory(val categoryQuery: String) {
    All(""),
    Archived(""),
    Unread("!unread"),
    Untagged("!untagged"),
}
