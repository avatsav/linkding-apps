package dev.avatsav.linkding

import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.avatsav.linkding.data.model.app.LaunchMode
import dev.avatsav.linkding.di.ComponentHolder
import dev.avatsav.linkding.di.DesktopAppComponent
import dev.avatsav.linkding.di.DesktopUiComponent
import dev.zacsweers.metro.createGraph
import java.awt.Desktop
import java.net.URI

fun main() = application {
  createGraph<DesktopAppComponent>()
    .also { ComponentHolder.components += it }
    .also { it.appInitializer.initialize() }

  val uiComponent: DesktopUiComponent =
    ComponentHolder.component<DesktopUiComponent.Factory>().create().also {
      ComponentHolder.components += it
    }

  val windowState =
    rememberWindowState(size = DpSize(450.dp, 900.dp), position = WindowPosition(Alignment.Center))

  Window(title = "Linkding", state = windowState, onCloseRequest = ::exitApplication) {
    uiComponent.appUi.Content(
      launchMode = LaunchMode.Normal,
      onOpenUrl = { launchUrl(it) },
      modifier = Modifier,
    )
  }
}

private fun launchUrl(url: String): Boolean {
  val desktop = Desktop.getDesktop()
  desktop.browse(URI.create(url))
  return true
}
