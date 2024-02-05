// package dev.avatsav.linkding.ui.bookmarks
//
// import android.content.res.Configuration.UI_MODE_NIGHT_YES
// import androidx.compose.foundation.layout.Arrangement
// import androidx.compose.foundation.layout.Column
// import androidx.compose.foundation.layout.Row
// import androidx.compose.foundation.layout.fillMaxWidth
// import androidx.compose.foundation.layout.padding
// import androidx.compose.foundation.text.KeyboardOptions
// import androidx.compose.material.icons.Icons
// import androidx.compose.material.icons.filled.Close
// import androidx.compose.material3.Button
// import androidx.compose.material3.CircularProgressIndicator
// import androidx.compose.material3.ExperimentalMaterial3Api
// import androidx.compose.material3.Icon
// import androidx.compose.material3.IconButton
// import androidx.compose.material3.LargeTopAppBar
// import androidx.compose.material3.MaterialTheme
// import androidx.compose.material3.OutlinedTextField
// import androidx.compose.material3.Scaffold
// import androidx.compose.material3.Text
// import androidx.compose.material3.TopAppBarDefaults
// import androidx.compose.material3.rememberTopAppBarState
// import androidx.compose.runtime.Composable
// import androidx.compose.runtime.getValue
// import androidx.compose.runtime.mutableStateOf
// import androidx.compose.runtime.remember
// import androidx.compose.runtime.setValue
// import androidx.compose.ui.Alignment
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.input.nestedscroll.nestedScroll
// import androidx.compose.ui.text.font.FontWeight
// import androidx.compose.ui.text.input.ImeAction
// import androidx.compose.ui.text.input.KeyboardType
// import androidx.compose.ui.text.style.TextOverflow
// import androidx.compose.ui.tooling.preview.Preview
// import androidx.compose.ui.unit.dp
// import androidx.lifecycle.compose.collectAsStateWithLifecycle
// import com.ramcosta.composedestinations.annotation.Destination
// import com.ramcosta.composedestinations.navigation.DestinationsNavigator
// import dev.avatsav.linkding.android.extensions.composableOnSuccess
// import dev.avatsav.linkding.android.theme.LinkdingTheme
// import dev.avatsav.linkding.android.ui.common.OutlinedPlaceholderTextField
// import dev.avatsav.linkding.android.ui.common.OutlinedTagsTextField
// import dev.avatsav.linkding.android.ui.common.SmallCircularProgressIndicator
// import dev.avatsav.linkding.android.ui.common.TagsTextFieldValue
// import dev.avatsav.linkding.domain.Bookmark
// import dev.avatsav.linkding.ui.AsyncState
// import dev.avatsav.linkding.ui.Content
// import dev.avatsav.linkding.ui.Fail
// import dev.avatsav.linkding.ui.bookmarks.AddBookmarkViewModel
// import dev.avatsav.linkding.ui.bookmarks.AddBookmarkViewState
// import dev.avatsav.linkding.ui.bookmarks.AddBookmarkViewState.SaveError
// import dev.avatsav.linkding.ui.bookmarks.UnfurlData
// import dev.avatsav.linkding.ui.onFail
// import dev.avatsav.linkding.ui.onLoading
// import dev.avatsav.linkding.ui.onSuccess
// import org.koin.androidx.compose.koinViewModel
//
// @Composable
// @Destination
// fun AddBookmarkScreen(
//    sharedUrl: String?,
//    navigator: DestinationsNavigator,
// ) {
//    val viewModel: AddBookmarkViewModel = koinViewModel()
//
//    AddBookmarkScreen(
//        sharedUrl = sharedUrl,
//        viewModel = viewModel,
//        onBookmarkSaved = {
//            navigator.popBackStack()
//        },
//        onClose = {
//            navigator.popBackStack()
//        },
//    )
// }
//
// /**
// * Using this composable from the activity called from share.
// */
// @Composable
// fun AddBookmarkScreen(
//    sharedUrl: String?,
//    onBookmarkSaved: () -> Unit = {},
//    onClose: () -> Unit = {},
// ) {
//    val viewModel: AddBookmarkViewModel = koinViewModel()
//
//    AddBookmarkScreen(
//        sharedUrl = sharedUrl,
//        viewModel = viewModel,
//        onBookmarkSaved = onBookmarkSaved,
//        onClose = onClose,
//    )
// }
//
// @Composable
// private fun AddBookmarkScreen(
//    sharedUrl: String?,
//    viewModel: AddBookmarkViewModel,
//    onBookmarkSaved: () -> Unit = {},
//    onClose: () -> Unit = {},
// ) {
//    if (sharedUrl != null) viewModel.urlChanged(sharedUrl)
//    AddBookmarkScreen(
//        modifier = Modifier,
//        sharedUrl = sharedUrl,
//        viewModel = viewModel,
//        onBookmarkSaved = onBookmarkSaved,
//        onClose = onClose,
//    )
// }
//
// @Composable
// private fun AddBookmarkScreen(
//    modifier: Modifier,
//    sharedUrl: String?,
//    viewModel: AddBookmarkViewModel,
//    onBookmarkSaved: () -> Unit,
//    onClose: () -> Unit,
// ) {
//    val state by viewModel.state.collectAsStateWithLifecycle()
//
//    state.saveState onSuccess {
//        onBookmarkSaved()
//        return
//    }
//    AddBookmarkScreen(
//        modifier = modifier,
//        sharedUrl = sharedUrl,
//        unfurlState = state.unfurlState,
//        saveState = state.saveState,
//        onClose = onClose,
//        onUrlChanged = viewModel::urlChanged,
//        onSave = viewModel::save,
//    )
// }
//
// @OptIn(ExperimentalMaterial3Api::class)
// @Composable
// private fun AddBookmarkScreen(
//    modifier: Modifier,
//    sharedUrl: String?,
//    unfurlState: AsyncState<UnfurlData, AddBookmarkViewState.UnfurlError>,
//    saveState: AsyncState<Bookmark, SaveError>,
//    onClose: () -> Unit,
//    onUrlChanged: (String) -> Unit,
//    onSave: (url: String, title: String?, description: String?, tags: List<String>) -> Unit,
// ) {
//    val scrollBehavior =
//        TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())
//
//    var url by remember { mutableStateOf(sharedUrl ?: "") }
//    val tagsValue by remember { mutableStateOf(TagsTextFieldValue()) }
//    var title by remember { mutableStateOf("") }
//    var description by remember { mutableStateOf("") }
//
//    Scaffold(
//        topBar = {
//            LargeTopAppBar(
//                title = { Text(text = "Add Bookmark") },
//                scrollBehavior = scrollBehavior,
//                navigationIcon = {
//                    IconButton(onClick = { onClose() }) {
//                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
//                    }
//                },
//            )
//        },
//        modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
//    ) { padding ->
//        Column(
//            modifier = modifier
//                .padding(padding)
//                .padding(horizontal = 16.dp)
//                .fillMaxWidth(),
//            verticalArrangement = Arrangement.spacedBy(16.dp),
//        ) {
//            OutlinedTextField(
//                modifier = Modifier.fillMaxWidth(),
//                value = url,
//                label = { Text(text = "URL") },
//                keyboardOptions = KeyboardOptions(
//                    autoCorrect = false,
//                    keyboardType = KeyboardType.Uri,
//                    imeAction = ImeAction.Next,
//                ),
//                onValueChange = { value ->
//                    url = value
//                    onUrlChanged(url)
//                },
//            )
//            OutlinedTagsTextField(
//                modifier = Modifier.fillMaxWidth(),
//                value = tagsValue,
//                label = { Text(text = "Tags") },
//                supportingText = {
//                    Text(text = "Enter any number of tags separated by space and without the hash (#). If a tag does not exist it will be automatically created.")
//                },
//            )
//            OutlinedPlaceholderTextField(
//                modifier = Modifier.fillMaxWidth(),
//                value = title,
//                label = { Text(text = "Title") },
//                supportingText = {
//                    Text(text = "Optional, leave empty to use title from website.")
//                },
//                trailingIcon = {
//                    unfurlState onLoading { SmallCircularProgressIndicator() }
//                },
//                placeholder = unfurlState composableOnSuccess { data: UnfurlData ->
//                    if (data.unfurledTitle == null) {
//                        null
//                    } else {
//                        {
//                            Text(
//                                text = data.unfurledTitle ?: "",
//                                maxLines = 4,
//                                overflow = TextOverflow.Ellipsis,
//                            )
//                        }
//                    }
//                },
//                onValueChange = { value -> title = value },
//            )
//            OutlinedPlaceholderTextField(
//                modifier = Modifier.fillMaxWidth(),
//                value = description,
//                label = { Text(text = "Description") },
//                placeholder = unfurlState composableOnSuccess { data: UnfurlData ->
//                    if (data.unfurledTitle == null) {
//                        null
//                    } else {
//                        {
//                            Text(
//                                text = data.unfurledDescription ?: "",
//                                maxLines = 4,
//                                overflow = TextOverflow.Ellipsis,
//                            )
//                        }
//                    }
//                },
//                trailingIcon = {
//                    unfurlState onLoading { SmallCircularProgressIndicator() }
//                },
//                supportingText = {
//                    Text(text = "Optional, leave empty to use description from website.")
//                },
//                onValueChange = { value -> description = value },
//            )
//            Row(
//                horizontalArrangement = Arrangement.spacedBy(8.dp),
//                verticalAlignment = Alignment.CenterVertically,
//            ) {
//                Button(
//                    onClick = {
//                        onSave(
//                            url,
//                            title,
//                            description,
//                            tagsValue.tags.map { it.value }.toList(),
//                        )
//                    },
//                ) {
//                    Text("Save")
//                }
//                saveState onLoading {
//                    CircularProgressIndicator()
//                }
//            }
//            saveState onFail { error ->
//                Text(
//                    text = error.message,
//                    fontWeight = FontWeight.Bold,
//                    color = MaterialTheme.colorScheme.error,
//                )
//            }
//        }
//    }
// }
//
// @Preview(showBackground = true)
// @Preview(uiMode = UI_MODE_NIGHT_YES)
// @Composable
// fun SetupConfigurationScreen_Preview() {
//    LinkdingTheme {
//        AddBookmarkScreen(
//            modifier = Modifier,
//            sharedUrl = "https://staffeng.com/guides/work-on-what-matters",
//            unfurlState = Content(
//                UnfurlData(
//                    unfurledUrl = "https://staffeng.com/guides/work-on-what-matters",
//                    unfurledTitle = "Work on what matters",
//                    unfurledDescription = "Stories of folks reaching Staff Engineer roles.",
//                ),
//            ),
//            saveState = Fail(SaveError("Error saving bookmark")),
//            onClose = {},
//            onUrlChanged = {},
//            onSave = { _, _, _, _ -> },
//        )
//    }
// }
