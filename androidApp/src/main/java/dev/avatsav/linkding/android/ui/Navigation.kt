package dev.avatsav.linkding.android.ui

internal sealed class Screen(val route: String) {
    object SetupCredentials : Screen("setup")
    object Bookmarks : Screen("bookmarks")
}