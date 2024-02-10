package dev.avatsav.linkding.ui

import com.slack.circuit.runtime.screen.Screen

abstract class LinkdingScreen(val name: String) : Screen

@CommonParcelize
data object RootScreen : LinkdingScreen("Root")

@CommonParcelize
data object SetupScreen : LinkdingScreen("Setup")

@CommonParcelize
data object BookmarksScreen : LinkdingScreen("Bookmarks")

@CommonParcelize
data class AddBookmarkScreen(val sharedUrl: String? = null) : LinkdingScreen("AddBookmark")

@CommonParcelize
data class UrlScreen(val url: String) : LinkdingScreen("UrlScreen")
