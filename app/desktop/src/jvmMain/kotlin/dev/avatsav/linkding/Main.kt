package dev.avatsav.linkding

import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.rememberCircuitNavigator
import dev.avatsav.linkding.ui.SetupScreen
import java.awt.Desktop
import java.net.URI

fun main() = application {
    val appComponent = remember { DesktopAppComponent.create() }

    val windowState = rememberWindowState(
        size = DpSize(450.dp, 900.dp),
        position = WindowPosition(Alignment.Center),
    )

    Window(
        title = "Linkding",
        state = windowState,
        onCloseRequest = ::exitApplication,
    ) {
        val uiComponent = remember(appComponent) { DesktopUiComponent.create(appComponent) }

        val backstack = rememberSaveableBackStack(root = SetupScreen)
        val navigator = rememberCircuitNavigator(backstack) { /* no-op */ }

        uiComponent.appUi.Content(
            backstack,
            navigator,
            { launchUrl(it) },
            Modifier,
        )
    }
}

private fun launchUrl(url: String): Boolean {
    val desktop = Desktop.getDesktop()
    desktop.browse(URI.create(url))
    return true
}
