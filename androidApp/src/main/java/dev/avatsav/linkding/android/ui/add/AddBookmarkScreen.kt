package dev.avatsav.linkding.android.ui.add

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.ExperimentalLifecycleComposeApi
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.avatsav.linkding.android.ui.components.TagsTextField
import dev.avatsav.linkding.android.ui.components.TagsTextFieldValue
import dev.avatsav.linkding.android.ui.theme.LinkdingTheme
import dev.avatsav.linkding.ui.AddBookmarkPresenter
import dev.avatsav.linkding.ui.UnfurlData
import dev.avatsav.linkding.ui.model.Async
import dev.avatsav.linkding.ui.model.Fail
import dev.avatsav.linkding.ui.model.Loading
import dev.avatsav.linkding.ui.model.Success

@Composable
fun AddBookmarkScreen(
    sharedLink: String?, presenter: AddBookmarkPresenter
) {
    if (sharedLink != null) presenter.setLink(sharedLink)
    DisposableEffect(presenter) {
        onDispose {
            presenter.clear()
        }
    }
    AddBookmarkScreen(
        modifier = Modifier,
        sharedLink = sharedLink,
        presenter = presenter,
    )
}

@OptIn(ExperimentalLifecycleComposeApi::class)
@Composable
fun AddBookmarkScreen(
    modifier: Modifier,
    sharedLink: String?,
    presenter: AddBookmarkPresenter,
) {
    val uiState by presenter.state.collectAsStateWithLifecycle()
    AddBookmarkScreen(
        modifier = modifier,
        sharedLink = sharedLink,
        unfurlState = uiState.unfurlState,
        saveState = uiState.saveState,
        onLinkChanged = presenter::setLink,
        onSave = presenter::save
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookmarkScreen(
    modifier: Modifier,
    sharedLink: String?,
    unfurlState: Async<UnfurlData>,
    saveState: Async<Unit>,
    onLinkChanged: (String) -> Unit,
    onSave: (url: String, title: String?, description: String?, tags: List<String>) -> Unit
) {
    var url by remember { mutableStateOf(sharedLink ?: "") }
    val tagsValue by remember { mutableStateOf(TagsTextFieldValue()) }
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
            TagsTextField(modifier = Modifier.fillMaxWidth(),
                value = tagsValue,
                label = { Text(text = "Tags") },
                supportingText = {
                    Text(text = "Enter any number of tags separated by space and without the hash (#). If a tag does not exist it will be automatically created.")
                })
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                value = title,
                label = { Text(text = "Title") },
                supportingText = {
                    Text(text = "Optional, leave empty to use title from website.")
                },
                trailingIcon = {
                    if (unfurlState is Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    }
                },
                placeholder = {
                    (unfurlState as? Success)?.run {
                        Text(
                            text = this().unfurledTitle ?: "",
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                maxLines = 2,
                onValueChange = { value ->
                    title = value
                })
            OutlinedTextField(modifier = Modifier.fillMaxWidth(),
                value = description,
                label = { Text(text = "Description") },
                maxLines = 4,
                placeholder = {
                    (unfurlState as? Success)?.run {
                        Text(
                            text = this().unfurledDescription ?: "",
                            maxLines = 4,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                },
                trailingIcon = {
                    if (unfurlState is Loading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    }
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
                Button(onClick = {
                    onSave(
                        url, title, description, tagsValue.tags.map { it.value }.toList()
                    )
                }) {
                    Text("Save")
                }
                if (saveState is Loading) {
                    CircularProgressIndicator()
                }
            }
            AnimatedVisibility(visible = saveState is Fail) {
                val errorMessage = (saveState as? Fail<Unit>)?.message ?: "Unknown error"
                Text(
                    text = errorMessage,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SetupConfigurationScreen_Preview() {
    LinkdingTheme {
        AddBookmarkScreen(modifier = Modifier,
            sharedLink = "https://staffeng.com/guides/work-on-what-matters",
            unfurlState = Success(
                UnfurlData(
                    unfurledUrl = "https://staffeng.com/guides/work-on-what-matters",
                    unfurledTitle = "Work on what matters",
                    unfurledDescription = "Stories of folks reaching Staff Engineer roles."
                )
            ),
            saveState = Fail("Error saving bookmark"),
            onLinkChanged = {},
            onSave = { _, _, _, _ -> })
    }
}