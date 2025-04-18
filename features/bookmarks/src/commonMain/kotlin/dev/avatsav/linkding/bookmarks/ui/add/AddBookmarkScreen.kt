package dev.avatsav.linkding.bookmarks.ui.add

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.slack.circuit.codegen.annotations.CircuitInject
import dev.avatsav.linkding.bookmarks.ui.add.AddBookmarkUiEvent.Close
import dev.avatsav.linkding.bookmarks.ui.add.AddBookmarkUiEvent.Save
import dev.avatsav.linkding.inject.UserScope
import dev.avatsav.linkding.ui.AddBookmarkScreen
import dev.avatsav.linkding.ui.compose.widgets.OutlinedTagsTextField
import dev.avatsav.linkding.ui.compose.widgets.PlaceholderVisualTransformation
import dev.avatsav.linkding.ui.compose.widgets.SmallCircularProgressIndicator
import dev.avatsav.linkding.ui.compose.widgets.TagsTextFieldValue
import kotlinx.coroutines.delay

private const val DebounceDelay = 500L

@CircuitInject(AddBookmarkScreen::class, UserScope::class)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddBookmark(state: AddBookmarkUiState, modifier: Modifier = Modifier) {
  val eventSink = state.eventSink

  val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(rememberTopAppBarState())

  var url by remember { mutableStateOf(state.sharedUrl ?: "") }
  val tagsValue by remember { mutableStateOf(TagsTextFieldValue()) }

  var title by remember { mutableStateOf("") }
  var description by remember { mutableStateOf("") }

  // Debouncing the url text field before unfurling/checking the url
  LaunchedEffect(url) {
    if (url.isBlank()) return@LaunchedEffect
    delay(DebounceDelay)
    eventSink(AddBookmarkUiEvent.CheckUrl(url))
  }

  Scaffold(
    modifier = modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
    topBar = {
      LargeTopAppBar(
        title = { Text(text = "Add Bookmark") },
        scrollBehavior = scrollBehavior,
        navigationIcon = {
          IconButton(onClick = { eventSink(Close) }) {
            Icon(imageVector = Icons.Default.Close, contentDescription = "Close")
          }
        },
      )
    },
  ) { padding ->
    Column(
      modifier =
        Modifier.padding(padding)
          .padding(horizontal = 16.dp)
          .animateContentSize()
          .verticalScroll(rememberScrollState())
          .fillMaxWidth(),
      verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
      OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = url,
        label = { Text(text = "URL") },
        keyboardOptions =
          KeyboardOptions(
            autoCorrectEnabled = false,
            keyboardType = KeyboardType.Uri,
            imeAction = ImeAction.Next,
          ),
        supportingText = {
          if (state.checkUrlResult?.alreadyBookmarked == true) {
            Text(text = "This URL is already bookmarked. Saving will update the existing bookmark.")
          }
        },
        onValueChange = { value -> url = value },
      )
      OutlinedTagsTextField(
        modifier = Modifier.fillMaxWidth(),
        value = tagsValue,
        label = { Text(text = "Tags") },
      )

      OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = title,
        label = { Text(text = "Title") },
        visualTransformation =
          PlaceholderVisualTransformation(
            text = state.checkUrlResult?.title ?: "",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          ),
        supportingText = { Text(text = "Optional, leave empty to use title from website.") },
        trailingIcon = { if (state.checkingUrl) SmallCircularProgressIndicator() },
        onValueChange = { title = it },
      )
      OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        value = description,
        label = { Text(text = "Description") },
        trailingIcon = { if (state.checkingUrl) SmallCircularProgressIndicator() },
        visualTransformation =
          PlaceholderVisualTransformation(
            text = state.checkUrlResult?.description ?: "",
            color = MaterialTheme.colorScheme.onSurfaceVariant,
          ),
        supportingText = { Text(text = "Optional, leave empty to use description from website.") },
        onValueChange = { description = it },
      )
      Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically,
      ) {
        Button(
          enabled = url.isNotBlank(),
          onClick = {
            eventSink(Save(url, title, description, tagsValue.tags.map { it.value }.toList()))
          },
        ) {
          Text("Save")
        }
        if (state.saving) CircularProgressIndicator()
      }
      if (state.errorMessage != null) {
        Text(
          text = state.errorMessage,
          fontWeight = FontWeight.Bold,
          color = MaterialTheme.colorScheme.error,
        )
      }
    }
  }
}
