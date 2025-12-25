package dev.avatsav.linkding.bookmarks.ui.add

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import dev.avatsav.linkding.bookmarks.ui.add.AddBookmarkUiEvent.Close
import dev.avatsav.linkding.bookmarks.ui.add.AddBookmarkUiEvent.Save
import dev.avatsav.linkding.navigation.LocalNavigator
import dev.avatsav.linkding.ui.compose.widgets.OutlinedTagsTextField
import dev.avatsav.linkding.ui.compose.widgets.PlaceholderVisualTransformation
import dev.avatsav.linkding.ui.compose.widgets.SmallCircularProgressIndicator
import dev.avatsav.linkding.ui.compose.widgets.TagsTextFieldValue
import dev.avatsav.linkding.viewmodel.ObserveEffects
import kotlinx.coroutines.delay

private const val DebounceDelay = 500L

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AddBookmarkScreen(viewModel: AddBookmarkViewModel, modifier: Modifier = Modifier) {
  val navigator = LocalNavigator.current
  val state by viewModel.models.collectAsStateWithLifecycle()
  val eventSink = viewModel::eventSink
  val snackbarHostState = remember { SnackbarHostState() }

  ObserveEffects(viewModel.effects) { effect ->
    when (effect) {
      AddBookmarkUiEffect.BookmarkSaved,
      AddBookmarkUiEffect.NavigateUp -> navigator.pop()
      AddBookmarkUiEffect.ExistingBookmarkFound ->
        snackbarHostState.showSnackbar("This bookmark already exists. Switched to edit mode.")
    }
  }

  AddBookmark(state, snackbarHostState, modifier, eventSink)
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AddBookmark(
  state: AddBookmarkUiState,
  snackbarHostState: SnackbarHostState,
  modifier: Modifier = Modifier,
  eventSink: (AddBookmarkUiEvent) -> Unit,
) {
  val currentEventSink by rememberUpdatedState(eventSink)
  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

  // Initialize URL from Shared mode or empty for New/Edit modes
  var url by remember { mutableStateOf(state.initialUrl) }
  val tagsValue = remember { TagsTextFieldValue() }

  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }
  var notes by remember { mutableStateOf("") }

  LaunchedEffect(state.existingBookmark) {
    if (state.isEditMode && state.existingBookmark != null) {
      url = state.existingBookmark.url
      title = state.existingBookmark.title
      description = state.existingBookmark.description
      notes = state.existingBookmark.notes
      tagsValue.replaceAll(state.existingBookmark.tags.toList())
    }
  }

  LaunchedEffect(url) {
    if (state.isEditMode || url.isBlank()) return@LaunchedEffect
    delay(DebounceDelay)
    currentEventSink(AddBookmarkUiEvent.CheckUrl(url))
  }

  val saveEnabled = url.isNotBlank() && !state.saving && !state.loading
  val screenTitle = if (state.isEditMode) "Edit Bookmark" else "Add Bookmark"

  Scaffold(
    modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    snackbarHost = { SnackbarHost(snackbarHostState) },
    topBar = {
      LargeFlexibleTopAppBar(
        title = { Text(text = screenTitle) },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
          IconButton(onClick = { eventSink(Close) }) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
          }
        },
      )
    },
    bottomBar = {
      AddBookmarkBottomBar(
        saving = state.saving,
        saveEnabled = saveEnabled,
        errorMessage = state.errorMessage,
        onSave = {
          eventSink(Save(url, title, description, notes, tagsValue.tags.map { it.value }.toList()))
        },
      )
    },
    contentWindowInsets = WindowInsets(),
  ) { padding ->
    LazyColumn(
      contentPadding = PaddingValues(horizontal = 12.dp, vertical = 12.dp),
      modifier = Modifier.padding(padding),
      verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
      item("url") {
        UrlTextField(
          url = url,
          onUrlChange = { url = it },
          isEditMode = state.isEditMode,
          alreadyBookmarked = state.checkUrlResult?.alreadyBookmarked == true,
        )
      }
      item("tags") {
        OutlinedTagsTextField(
          modifier = Modifier.fillMaxWidth(),
          value = tagsValue,
          label = { Text(text = "Tags") },
        )
      }
      item("title") {
        TitleTextField(
          title = title,
          onTitleChange = { title = it },
          isEditMode = state.isEditMode,
          checkingUrl = state.checkingUrl,
          placeholderTitle = state.checkUrlResult?.title.orEmpty(),
        )
      }
      item("description") {
        DescriptionTextField(
          description = description,
          onDescriptionChange = { description = it },
          isEditMode = state.isEditMode,
          checkingUrl = state.checkingUrl,
          placeholderDescription = state.checkUrlResult?.description.orEmpty(),
        )
      }
      item("notes") {
        OutlinedTextField(
          modifier = Modifier.fillMaxWidth(),
          value = notes,
          label = { Text(text = "Notes") },
          minLines = 2,
          onValueChange = { notes = it },
        )
      }
    }
  }
}

@Composable
private fun AddBookmarkBottomBar(
  saving: Boolean,
  saveEnabled: Boolean,
  errorMessage: String?,
  onSave: () -> Unit,
) {
  BottomAppBar(
    modifier = Modifier.imePadding(),
    floatingActionButton = {
      Button(
        modifier = Modifier.defaultMinSize(minWidth = 56.dp, minHeight = 48.dp),
        enabled = saveEnabled,
        onClick = onSave,
      ) {
        if (saving) SmallCircularProgressIndicator() else Text("Save")
      }
    },
    actions = {
      if (errorMessage != null) {
        Text(
          modifier = Modifier.padding(horizontal = 16.dp),
          text = errorMessage,
          style = MaterialTheme.typography.bodyMedium,
          color = MaterialTheme.colorScheme.error,
        )
      }
    },
  )
}

@Composable
private fun UrlTextField(
  url: String,
  onUrlChange: (String) -> Unit,
  isEditMode: Boolean,
  alreadyBookmarked: Boolean,
) {
  OutlinedTextField(
    modifier = Modifier.fillMaxWidth(),
    value = url,
    label = { Text(text = "URL") },
    enabled = !isEditMode,
    readOnly = isEditMode,
    keyboardOptions =
      KeyboardOptions(
        autoCorrectEnabled = false,
        keyboardType = KeyboardType.Uri,
        imeAction = ImeAction.Next,
      ),
    supportingText =
      if (isEditMode) {
        @Composable { Text(text = "URL cannot be changed when editing.") }
      } else if (alreadyBookmarked) {
        @Composable {
          Text(text = "This URL is already bookmarked. Saving will update the existing bookmark.")
        }
      } else null,
    onValueChange = onUrlChange,
  )
}

@Composable
private fun TitleTextField(
  title: String,
  onTitleChange: (String) -> Unit,
  isEditMode: Boolean,
  checkingUrl: Boolean,
  placeholderTitle: String,
) {
  val visualTransformation =
    if (!isEditMode && title.isEmpty()) {
      PlaceholderVisualTransformation(
        text = placeholderTitle,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    } else {
      VisualTransformation.None
    }

  OutlinedTextField(
    modifier = Modifier.fillMaxWidth(),
    value = title,
    label = { Text(text = "Title") },
    visualTransformation = visualTransformation,
    trailingIcon = { if (checkingUrl) SmallCircularProgressIndicator() },
    onValueChange = onTitleChange,
  )
}

@Composable
private fun DescriptionTextField(
  description: String,
  onDescriptionChange: (String) -> Unit,
  isEditMode: Boolean,
  checkingUrl: Boolean,
  placeholderDescription: String,
) {
  val visualTransformation =
    if (!isEditMode && description.isEmpty()) {
      PlaceholderVisualTransformation(
        text = placeholderDescription,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
      )
    } else {
      VisualTransformation.None
    }

  OutlinedTextField(
    modifier = Modifier.fillMaxWidth(),
    value = description,
    label = { Text(text = "Description") },
    trailingIcon = { if (checkingUrl) SmallCircularProgressIndicator() },
    visualTransformation = visualTransformation,
    minLines = 2,
    onValueChange = onDescriptionChange,
  )
}
