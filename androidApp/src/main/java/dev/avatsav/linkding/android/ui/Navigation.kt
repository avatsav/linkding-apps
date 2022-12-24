package dev.avatsav.linkding.android.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dev.avatsav.linkding.android.ui.bookmarks.BookmarksScreen
import dev.avatsav.linkding.android.ui.setup.SetupScreen
import org.koin.androidx.compose.get

internal sealed class Screen(open val route: String) {
    object Setup : Screen("setup")
    object Bookmarks : Screen("bookmarks")
}

@Composable
fun LinkdingNavigation(
    credentialsSetup: Boolean,
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = if (credentialsSetup) Screen.Bookmarks.route else Screen.Setup.route,
        modifier = modifier
    ) {
        addSetupScreen(navController)
        addBookmarksScreen(navController)
    }
}

private fun NavGraphBuilder.addSetupScreen(navController: NavController) {
    composable(Screen.Setup.route) {
        SetupScreen(get())
    }
}

private fun NavGraphBuilder.addBookmarksScreen(navController: NavController) {
    composable(Screen.Bookmarks.route) {
        BookmarksScreen(onAddBookmark = { })
    }
}

