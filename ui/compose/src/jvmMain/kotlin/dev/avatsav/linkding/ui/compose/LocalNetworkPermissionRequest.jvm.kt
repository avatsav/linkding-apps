package dev.avatsav.linkding.ui.compose

import androidx.compose.runtime.Composable

@Composable
actual fun rememberLocalNetworkPermissionRequest(
    onResult: (Boolean) -> Unit
): LocalNetworkPermissionRequest =
    LocalNetworkPermissionRequest(opensSettings = false, launch = { onResult(true) })
