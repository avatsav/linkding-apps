package dev.avatsav.linkding.ui

import com.slack.circuit.runtime.screen.PopResult
import com.slack.circuit.runtime.screen.Screen
import dev.avatsav.linkding.parcelize.Parcelable
import dev.avatsav.linkding.parcelize.Parcelize

abstract class LinkdingScreen(val name: String) : Screen

@Parcelize
data object AuthScreen : LinkdingScreen("Auth")

@Parcelize
data object BookmarksScreen : LinkdingScreen("Bookmarks")

@Parcelize
data class AddBookmarkScreen(val sharedUrl: String? = null) : LinkdingScreen("AddBookmark")

@Parcelize
data class UrlScreen(val url: String) : LinkdingScreen("UrlScreen")

@Parcelize
data object SettingsScreen : LinkdingScreen("Settings")

@Parcelize
data class TagsScreenParam(val id: Long, val name: String) : Parcelable

@Parcelize
data class TagsScreen(val selectedTags: List<TagsScreenParam>) : LinkdingScreen("Tags")

sealed interface TagsScreenResult : PopResult {
    @Parcelize
    data class Selected(val tag: TagsScreenParam) : TagsScreenResult

    @Parcelize
    data object Dismissed : TagsScreenResult
}
