package dev.avatsav.linkding.ui

import com.slack.circuit.runtime.screen.PopResult
import com.slack.circuit.runtime.screen.Screen
import dev.avatsav.linkding.data.model.Tag

abstract class LinkdingScreen(val name: String) : Screen

@CommonParcelize
data class RootScreen(val sharedUrl: String?) : LinkdingScreen("Root")

@CommonParcelize
data object SetupScreen : LinkdingScreen("Setup")

@CommonParcelize
data object BookmarksScreen : LinkdingScreen("Bookmarks")

@CommonParcelize
data class AddBookmarkScreen(val sharedUrl: String? = null) : LinkdingScreen("AddBookmark")

@CommonParcelize
data class UrlScreen(val url: String) : LinkdingScreen("UrlScreen")

@CommonParcelize
data object SettingsScreen : LinkdingScreen("Settings")

@CommonParcelize
data class TagsScreenParam(val id: Long, val name: String) : CommonParcelable

@CommonParcelize
data class TagsScreen(val selectedTags: List<TagsScreenParam>) : LinkdingScreen("Tags")

sealed interface TagsScreenResult : PopResult {
    @CommonParcelize
    data class Selected(val tag: TagsScreenParam) : TagsScreenResult

    @CommonParcelize
    data object Dismissed : TagsScreenResult
}

fun Tag.mapToScreenParam(): TagsScreenParam {
    return TagsScreenParam(id, name)
}

fun TagsScreenParam.mapToTag(): Tag {
    return Tag(id, name)
}
