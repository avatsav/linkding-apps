package dev.avatsav.linkding.ui.tags

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.avatsav.linkding.ui.theme.LinkdingTheme

@Preview
@Composable
fun TagsScreenPreview() {
    LinkdingTheme {
        Tags(
            state = TagsUiState {},
        )
    }
}
