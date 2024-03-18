package dev.avatsav.linkding.ui.shared

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.ui.compose.widgets.OutlinedTagsTextField
import dev.avatsav.linkding.ui.compose.widgets.Tag
import dev.avatsav.linkding.ui.compose.widgets.TagsTextFieldValue

@Preview(showBackground = true)
@Composable
fun OutlinedTagsTextFieldPreview() {
    val tagsField = remember {
        TagsTextFieldValue().also {
            it.addTag(Tag("jetpack"))
            it.addTag(Tag("compose"))
            it.addTag(Tag("hello"))
            it.addTag(Tag("world"))
        }
    }
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        OutlinedTagsTextField(
            value = tagsField,
            modifier = Modifier.fillMaxWidth(),
            label = { Text(text = "Outlined Tags") },
        )
    }
}
