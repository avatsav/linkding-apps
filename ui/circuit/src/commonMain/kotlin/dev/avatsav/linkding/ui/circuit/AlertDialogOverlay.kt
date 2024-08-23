// Copyright (C) 2022 Slack Technologies, LLC
// SPDX-License-Identifier: Apache-2.0

package dev.avatsav.linkding.ui.circuit

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AlertDialogDefaults
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.window.DialogProperties
import com.slack.circuit.overlay.Overlay
import com.slack.circuit.overlay.OverlayNavigator
import com.slack.circuitx.overlays.DialogResult

/** An overlay that shows an basic [AlertDialog]. */
@ExperimentalMaterial3Api
class BasicAlertDialogOverlay<Model : Any, Result : Any>(
    private val model: Model,
    private val onDismissRequest: () -> Result,
    private val properties: DialogProperties = DialogProperties(),
    private val content: @Composable (Model, OverlayNavigator<Result>) -> Unit,
) : Overlay<Result> {
    @Composable
    override fun Content(navigator: OverlayNavigator<Result>) {
        BasicAlertDialog(
            onDismissRequest = {
                // This is apparently as close as we can get to an "onDismiss" callback, which
                // unfortunately has no animation
                navigator.finish(onDismissRequest())
            },
            properties = properties,
            content = {
                Surface(
                    shape = AlertDialogDefaults.shape,
                    color = AlertDialogDefaults.containerColor,
                    tonalElevation = AlertDialogDefaults.TonalElevation,
                ) {
                    content(model, navigator::finish)
                }
            },
        )
    }
}

typealias OnClick = () -> Unit

/**
 * An overlay that shows an [AlertDialog] with configurable inputs.
 *
 * @see AlertDialog for docs on the parameters
 */
@ExperimentalMaterial3Api
public fun alertDialogOverlay(
    confirmButton: @Composable (onClick: OnClick) -> Unit,
    icon: @Composable (() -> Unit)? = null,
    title: @Composable (() -> Unit)? = null,
    text: @Composable (() -> Unit)? = null,
    dismissButton: (@Composable (onClick: OnClick) -> Unit)?,
    properties: DialogProperties = DialogProperties(),
): BasicAlertDialogOverlay<*, DialogResult> = BasicAlertDialogOverlay(
    model = Unit,
    onDismissRequest = { DialogResult.Dismiss },
    properties = properties,
) { _, navigator ->
    AlertDialog(
        onDismissRequest = { navigator.finish(DialogResult.Dismiss) },
        icon = icon,
        title = title,
        text = text,
        confirmButton = { confirmButton { navigator.finish(DialogResult.Confirm) } },
        dismissButton =
        dismissButton?.let { dismissButton -> { dismissButton { navigator.finish(DialogResult.Cancel) } } },
        properties = properties,
    )
}
