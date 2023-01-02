package dev.avatsav.linkding.android.ui.components

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.android.ui.components.tags.Tag
import dev.avatsav.linkding.android.ui.components.tags.TagsTextField
import dev.avatsav.linkding.android.ui.components.tags.TagsTextFieldValue
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
                    .fillMaxSize()
            ) {
                val tagsValue = remember { TagsTextFieldValue(tags = emptyList()) }

                var textValue by remember { mutableStateOf(TextFieldValue()) }
                val onValueChange: (TextFieldValue) -> Unit = { textValue = it }
                TagsTextField(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    value = tagsValue,
                    label = { Text(text = "Tags") },
                )
                OutlinedTextField(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth(),
                    value = textValue,
                    onValueChange = onValueChange,
                    label = { Text(text = "Another") })
            }
        }
    }
}

val defaultTags = listOf(
    Tag("staff-eng"), Tag("engineering-mgmt"), Tag("kotlin"), Tag("multiplatform")
)