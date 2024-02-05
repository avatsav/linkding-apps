package dev.avatsav.linkding.ui.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.slack.circuit.backstack.SaveableBackStack
import com.slack.circuit.runtime.Navigator
import dev.avatsav.linkding.data.model.ApiConfiguration
import dev.avatsav.linkding.ui.BookmarksScreen
import dev.avatsav.linkding.ui.SetupScreen

@Composable
fun Root(
    backstack: SaveableBackStack,
    navigator: Navigator,
    apiConfiguration: ApiConfiguration,
    modifier: Modifier = Modifier,
) {
    val rootScreen by remember(backstack) {
        derivedStateOf { backstack.last().screen }
    }

    when (apiConfiguration) {
        is ApiConfiguration.Linkding -> navigator.goTo(BookmarksScreen)
            .also { navigator.resetRoot(BookmarksScreen) }

        ApiConfiguration.NotSet -> navigator.goTo(SetupScreen)
            .also { navigator.resetRoot(SetupScreen) }
    }
}
