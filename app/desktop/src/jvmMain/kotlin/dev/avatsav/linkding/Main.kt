package dev.avatsav.linkding

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowPosition
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import dev.avatsav.linkding.data.model.app.LaunchMode
import dev.avatsav.linkding.di.DesktopAppGraph
import dev.avatsav.linkding.di.DesktopUiGraph
import dev.avatsav.linkding.di.GraphHolder
import dev.zacsweers.metro.createGraph
import dev.zacsweers.metrox.viewmodel.LocalMetroViewModelFactory
import java.awt.Desktop
import java.net.URI

fun main() = application {
  val appGraph =
    createGraph<DesktopAppGraph>()
      .also { GraphHolder.components += it }
      .also { it.appInitializer.initialize() }

  val uiGraph: DesktopUiGraph =
    GraphHolder.component<DesktopUiGraph.Factory>().create().also { GraphHolder.components += it }

  val windowState =
    rememberWindowState(size = DpSize(450.dp, 900.dp), position = WindowPosition(Alignment.Center))

  val metroViewModelFactory = appGraph.metroViewModelFactory

  Window(title = "Linkding", state = windowState, onCloseRequest = ::exitApplication) {
    CompositionLocalProvider(LocalMetroViewModelFactory provides metroViewModelFactory) {
      uiGraph.appUi.Content(
        launchMode = LaunchMode.Normal,
        onOpenUrl = { launchUrl(it) },
        modifier = Modifier,
      )
    }
  }
}

private fun launchUrl(url: String): Boolean {
  val desktop = Desktop.getDesktop()
  desktop.browse(URI.create(url))
  return true
}
