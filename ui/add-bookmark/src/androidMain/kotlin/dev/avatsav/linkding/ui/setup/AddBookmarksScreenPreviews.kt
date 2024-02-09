package dev.avatsav.linkding.ui.setup

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import dev.avatsav.linkding.ui.add.AddBookmarkScreen
import dev.avatsav.linkding.ui.add.AddBookmarkUiState

@Preview
@Composable
fun AddBookmarkScreenPreview() {
    AddBookmarkScreen(
        state = AddBookmarkUiState {},
        modifier = Modifier,
    )
}
