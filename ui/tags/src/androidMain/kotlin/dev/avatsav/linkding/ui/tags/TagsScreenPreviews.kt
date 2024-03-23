package dev.avatsav.linkding.ui.tags

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import dev.avatsav.linkding.ui.theme.LinkdingTheme
import kotlinx.collections.immutable.toImmutableList

@Preview
@Composable
fun TagsScreenPreview() {
    LinkdingTheme {
        Tags(
            state = TagsUiState(
                selectedTags = emptyList(),
                tags = tags,
            ) {},
        )
    }
}

private val tags = mutableListOf<String>().also { tags ->
    repeat(50) {
        tags.add("Tag ${it + 1}")
    }
}.toImmutableList()
