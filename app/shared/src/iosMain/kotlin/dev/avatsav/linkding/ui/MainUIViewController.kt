// Copyright 2020, Google LLC, Christopher Banes and the Tivi project contributors
// SPDX-License-Identifier: Apache-2.0

package dev.avatsav.linkding.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.LocalUIViewController
import androidx.compose.ui.window.ComposeUIViewController
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.rememberCircuitNavigator
import me.tatarka.inject.annotations.Inject
import platform.Foundation.NSURL
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIViewController

typealias MainUIViewController = () -> UIViewController

@Inject
@Suppress("ktlint:standard:function-naming")
fun MainUIViewController(appUi: AppUi): UIViewController = ComposeUIViewController {
  val backstack = rememberSaveableBackStack(root = AuthScreen)
  val navigator = rememberCircuitNavigator(backstack, onRootPop = { /* no-op */ })
  val uiViewController = LocalUIViewController.current
  appUi.Content(backstack, navigator, { uiViewController.launchUrl(it) }, Modifier.fillMaxSize())
}

private fun UIViewController.launchUrl(url: String): Boolean {
  val safari = SFSafariViewController(NSURL(string = url))
  presentViewController(safari, animated = true, completion = null)
  return true
}
