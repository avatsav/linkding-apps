package dev.avatsav.linkding.android.ui.add

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.android.ui.theme.LinkdingTheme
import dev.avatsav.linkding.ui.AddBookmarkPresenter
import dev.avatsav.linkding.ui.AddBookmarkPresenter.ViewState

@Composable
fun AddBookmarkScreen(presenter: AddBookmarkPresenter) {
    val uiState by presenter.state.collectAsState()
    DisposableEffect(presenter) {
        onDispose {
            presenter.clear()
        }
    }
    AddBookmarkScreen(
        uiState = uiState, onLinkChanged = presenter::setLink, onSave = presenter::save
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookmarkScreen(
    modifier: Modifier = Modifier,
    uiState: ViewState,
    onLinkChanged: (String) -> Unit,
    onSave: (AddBookmarkPresenter.AddBookmarkRequest) -> Unit
) {
    var url by remember { mutableStateOf("") }
    var tags by remember { mutableStateOf("") }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    Scaffold(topBar = {
        LargeTopAppBar(title = { Text(text = "Add Bookmark") })
    }) { padding ->
        Column(
            modifier = modifier
                .padding(padding)
                .padding(horizontal = 16.dp)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                value = url,
                enabled = !uiState.loading,
                label = { Text(text = "URL") },
                keyboardOptions = KeyboardOptions(
                    autoCorrect = false,
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next,
                ),
                onValueChange = { value ->
                    url = value
                    onLinkChanged(url)
                })
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                value = tags,
                enabled = !uiState.loading,
                label = { Text(text = "Tags") },
                supportingText = {
                    Text(text = "Enter any number of tags separated by space and without the hash (#). If a tag does not exist it will be automatically created.")
                },
                onValueChange = { value ->
                    tags = value
                })
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                value = title,
                enabled = !uiState.loading,
                label = { Text(text = "Title") },
                supportingText = {
                    Text(text = "Optional, leave empty to use title from website.")
                },
                placeholder = {
                    Text(
                        text = uiState.unfluredTitle ?: "",
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                maxLines = 2,
                onValueChange = { value ->
                    title = value
                })
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                value = description,
                enabled = !uiState.loading,
                label = { Text(text = "Description") },
                maxLines = 4,
                placeholder = {
                    Text(
                        text = uiState.unfluredDescription ?: "",
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                supportingText = {
                    Text(text = "Optional, leave empty to use description from website.")
                },
                onValueChange = { value ->
                    description = value
                })
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(enabled = !uiState.loading, onClick = {
                    onSave(
                        AddBookmarkPresenter.AddBookmarkRequest(
                            url = url, tags = setOf(), title = title, description = description
                        )
                    )
                }) {
                    Text("Save")
                }
                if (uiState.loading) {
                    CircularProgressIndicator(
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SetupConfigurationScreen_Preview() {
    LinkdingTheme {
        AddBookmarkScreen(uiState = ViewState(
            loading = false,
            "https://staffeng.com/guides/work-on-what-matters",
            "Work on what matters",
            "Stories of folks reaching Staff Engineer roles."
        ), onLinkChanged = {}, onSave = {})
    }
}