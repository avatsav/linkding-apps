package dev.avatsav.linkding.android.debug

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.android.ui.components.OutlinedPlaceholderTextField
import dev.avatsav.linkding.android.ui.components.OutlinedTagsTextField
import dev.avatsav.linkding.android.ui.components.Tag
import dev.avatsav.linkding.android.ui.components.TagsTextFieldValue
import dev.avatsav.linkding.android.ui.theme.LinkdingTheme

class PlaygroundActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PlaygroundScreen()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaygroundScreen() {
    LinkdingTheme {
        Scaffold(modifier = Modifier.fillMaxSize(),
            topBar = { TopAppBar(title = { Text(text = "Playground") }) }) { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp)
                    .fillMaxSize(), verticalArrangement = Arrangement.spacedBy(36.dp)
            ) {
                val tagsValue = remember { TagsTextFieldValue(tags = defaultTags) }
                var text by remember { mutableStateOf("") }
                OutlinedTagsTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = tagsValue,
                    label = { Text(text = "Tags") },
                )
                OutlinedPlaceholderTextField(modifier = Modifier.fillMaxWidth(),
                    value = text,
                    onValueChange = { value -> text = value },
                    label = { Text(text = "Description") },
                    placeholder = { Text(text = "Stories of folks reaching Staff Engineer roles.") })
            }
        }
    }
}

val defaultTags = listOf(
    Tag("staff-eng"), Tag("engineering-mgmt"), Tag("kotlin"), Tag("multiplatform")
)