package dev.avatsav.linkding

import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.rememberCircuitNavigator
import dev.avatsav.linkding.ui.RootScreen

fun main() = application {
    val appComponent = remember { DesktopAppComponent.create() }

    Window(
        title = "Linkding",
        onCloseRequest = ::exitApplication,
    ) {
        val uiComponent = remember(appComponent) { DesktopUiComponent.create(appComponent) }

        val backstack = rememberSaveableBackStack(root = RootScreen(null))
        val navigator = rememberCircuitNavigator(backstack) { /* no-op */ }

        uiComponent.appContent(
            backstack,
            navigator,
            { /*TODO*/ },
            Modifier,
        )
    }
}
