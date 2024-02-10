package dev.avatsav.linkding.ui.setup

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Preview
@Composable
fun SetupScreenPreview() {
    SetupApiConfig(
        state = SetupUiState {},
        modifier = Modifier,
    )
}
