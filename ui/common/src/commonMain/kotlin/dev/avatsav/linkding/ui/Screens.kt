package dev.avatsav.linkding.ui

import com.slack.circuit.runtime.screen.Screen

abstract class LinkdingScreen(val name: String) : Screen

@CommonParcelize
data object SetupScreen : LinkdingScreen("Setup")

@CommonParcelize
data object BookmarkListScreen : LinkdingScreen("BookmarkList")

@CommonParcelize
data class BookmarkDetailScreen(val id: String) : LinkdingScreen("BookmarkDetail")
