// Copyright 2020, Google LLC, Christopher Banes and the Tivi project contributors
// SPDX-License-Identifier: Apache-2.0

package dev.avatsav.linkding.ui.root

import androidx.compose.ui.Modifier
import androidx.compose.ui.interop.LocalUIViewController
import androidx.compose.ui.window.ComposeUIViewController
import com.slack.circuit.backstack.rememberSaveableBackStack
import com.slack.circuit.foundation.rememberCircuitNavigator
import dev.avatsav.linkding.ui.RootScreen
import me.tatarka.inject.annotations.Inject
import platform.Foundation.NSURL
import platform.SafariServices.SFSafariViewController
import platform.UIKit.UIViewController

typealias MainUIViewController = () -> UIViewController

@Inject
@Suppress("ktlint:standard:function-naming")
fun MainUIViewController(
    appContent: AppContent,
): UIViewController = ComposeUIViewController {
    val backstack = rememberSaveableBackStack(root = RootScreen(null))
    val navigator = rememberCircuitNavigator(backstack, onRootPop = { /* no-op */ })
    val uiViewController = LocalUIViewController.current
    appContent(
        backstack,
        navigator,
        { url ->
            val safari = SFSafariViewController(NSURL(string = url))
            uiViewController.presentViewController(safari, animated = true, completion = null)
        },
        Modifier,
    )
}
