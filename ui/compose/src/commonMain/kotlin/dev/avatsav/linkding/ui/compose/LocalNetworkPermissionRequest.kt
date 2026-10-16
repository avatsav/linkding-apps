package dev.avatsav.linkding.ui.compose

import androidx.compose.runtime.Composable

/** An activity-owned permission action; launch is invoked only by a user action/effect. */
data class LocalNetworkPermissionRequest(val opensSettings: Boolean, val launch: () -> Unit)

@Composable
expect fun rememberLocalNetworkPermissionRequest(
    onResult: (Boolean) -> Unit
): LocalNetworkPermissionRequest
