// Copyright 2020, Google LLC, Christopher Banes and the Tivi project contributors
// SPDX-License-Identifier: Apache-2.0

package dev.avatsav.linkding.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.uikit.LocalUIViewController
import androidx.compose.ui.window.ComposeUIViewController
import dev.avatsav.linkding.data.model.app.LaunchMode
import dev.zacsweers.metro.Inject
import platform.Foundation.NSURL
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIViewController

@Inject
class MainUIViewControllerFactory(private val appUi: AppUi) {
  fun create(): UIViewController = ComposeUIViewController {
    val uiViewController = LocalUIViewController.current
    appUi.Content(
      LaunchMode.Normal,
      onOpenUrl = { uiViewController.launchUrl(it) },
      onRootPop = { /* NO OP */ },
      modifier = Modifier.fillMaxSize(),
    )
  }
}

private fun UIViewController.launchUrl(url: String): Boolean {
  val safari = SFSafariViewController(NSURL(string = url))
  presentViewController(safari, animated = true, completion = null)
  return true
}
