package dev.avatsav.linkding.bookmarks.ui.list

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.input.clearText
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.bookmarks.ui.list.widgets.SearchHistoryHeader
import dev.avatsav.linkding.bookmarks.ui.list.widgets.SearchHistoryItem
import dev.avatsav.linkding.data.model.SearchHistory
import dev.avatsav.linkding.ui.theme.Material3ShapeDefaults
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun BookmarksSearchBar(
  query: String,
  filteredHistory: List<SearchHistory>,
  onSearch: (String) -> Unit,
  onSelectHistory: (SearchHistory) -> Unit,
  onClearSearch: () -> Unit,
  onClearHistory: () -> Unit,
  onShowSettings: () -> Unit,
  modifier: Modifier = Modifier,
) {
  var expanded by remember { mutableStateOf(false) }
  val textFieldState = rememberTextFieldState(query)
  val scope = rememberCoroutineScope()
  val currentOnSearch by rememberUpdatedState(onSearch)
  val currentOnSelectHistory by rememberUpdatedState(onSelectHistory)
  val currentOnClearSearch by rememberUpdatedState(onClearSearch)

  // Sync text field with external query changes (e.g., from search history selection)
  LaunchedEffect(query) {
    if (textFieldState.text.toString() != query) {
      textFieldState.edit {
        replace(0, length, query)
        selection = TextRange(query.length)
      }
    }
  }

  // When collapsing with empty query, clear search
  LaunchedEffect(expanded) {
    if (!expanded && textFieldState.text.isBlank()) {
      currentOnClearSearch()
    }
  }

  Box(modifier = modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter) {
    DockedSearchBar(
      inputField = {
        SearchBarDefaults.InputField(
          state = textFieldState,
          onSearch = { searchQuery ->
            scope.launch {
              currentOnSearch(searchQuery)
              expanded = false
            }
          },
          expanded = expanded,
          onExpandedChange = { expanded = it },
          placeholder = { Text(text = "Search bookmarks") },
          leadingIcon = {
            if (expanded) {
              IconButton(onClick = { expanded = false }) {
                Icon(Icons.AutoMirrored.Default.ArrowBack, contentDescription = "Close search")
              }
            } else {
              Icon(Icons.Default.Search, contentDescription = null)
            }
          },
          trailingIcon = {
            if (expanded && textFieldState.text.isNotEmpty()) {
              IconButton(onClick = { textFieldState.clearText() }) {
                Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
              }
            } else if (!expanded) {
              IconButton(onClick = onShowSettings) {
                Icon(imageVector = Icons.Default.Settings, contentDescription = "Settings")
              }
            }
          },
        )
      },
      expanded = expanded,
      onExpandedChange = { expanded = it },
      modifier = Modifier.padding(horizontal = 16.dp),
    ) {
      // Show filtered search history when expanded
      if (filteredHistory.isNotEmpty()) {
        LazyColumn(contentPadding = PaddingValues(vertical = 8.dp)) {
          item(key = "history_header") {
            SearchHistoryHeader(onClearHistory = onClearHistory, modifier = Modifier.animateItem())
          }
          items(
            count = filteredHistory.size,
            key = { "history_${filteredHistory[it].query}_${filteredHistory[it].modified}" },
          ) { index ->
            val historyItem = filteredHistory[index]
            SearchHistoryItem(
              searchHistory = historyItem,
              onClick = {
                currentOnSelectHistory(historyItem)
                expanded = false
              },
              modifier = Modifier.animateItem(),
              shape = Material3ShapeDefaults.itemShape(index, filteredHistory.size),
            )
          }
        }
      }
    }
  }
}
