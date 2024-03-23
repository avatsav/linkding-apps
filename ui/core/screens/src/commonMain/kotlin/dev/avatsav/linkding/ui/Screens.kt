package dev.avatsav.linkding.ui

import com.slack.circuit.runtime.screen.PopResult
import com.slack.circuit.runtime.screen.Screen

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
data class TagsScreen(val selectedTags: List<String>) : LinkdingScreen("Tags") {
    sealed interface Result : PopResult {
        @CommonParcelize
        data class Selected(val tag: String) : Result

        @CommonParcelize
        data object Dismissed : Result
    }
}
