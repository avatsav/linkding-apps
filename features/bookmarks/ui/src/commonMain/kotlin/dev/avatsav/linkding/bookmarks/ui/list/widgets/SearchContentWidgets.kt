package dev.avatsav.linkding.bookmarks.ui.list.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.History
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.data.model.BookmarkCategory
import dev.avatsav.linkding.data.model.SearchHistory

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SearchHistoryItem(
  searchHistory: SearchHistory,
  onClick: () -> Unit,
  modifier: Modifier = Modifier,
  shape: Shape = MaterialTheme.shapes.medium,
) {
  Surface(
    modifier = modifier.padding(horizontal = 12.dp, vertical = 1.dp),
    shape = shape,
    tonalElevation = 4.dp,
  ) {
    ListItem(
      modifier = Modifier.defaultMinSize(minHeight = 36.dp).clickable { onClick() },
      headlineContent = {
        Text(
          text = searchHistory.query,
          style = MaterialTheme.typography.bodyMedium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      },
      leadingContent = {
        Icon(imageVector = Icons.Default.History, contentDescription = "Search History")
      },
      supportingContent =
        if (searchHistory.hasFilterCriteria()) {
          {
            FlowRow(
              modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
              horizontalArrangement = Arrangement.spacedBy(8.dp),
              verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
              // Filter chip (if not All)
              if (searchHistory.bookmarkCategory != BookmarkCategory.All) {
                FilterCriteriaItem(searchHistory.bookmarkCategory.name)
              }

              // Tag chips
              searchHistory.selectedTags.forEach { tag -> FilterCriteriaItem(tag.name) }
            }
          }
        } else null,
    )
  }
}

@Composable
private fun FilterCriteriaItem(name: String, modifier: Modifier = Modifier) {
  Text(
    name,
    modifier =
      modifier
        .background(
          color = AssistChipDefaults.assistChipColors().containerColor,
          shape = MaterialTheme.shapes.small,
        )
        .border(
          border = AssistChipDefaults.assistChipBorder(true),
          shape = AssistChipDefaults.shape,
        )
        .padding(horizontal = 8.dp, vertical = 4.dp),
    style = MaterialTheme.typography.labelSmall,
  )
}

@Composable
fun EmptySearchResults(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.fillMaxWidth().padding(vertical = 32.dp, horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Text(text = "No matches found", style = MaterialTheme.typography.titleMedium)
    Text(
      text = "Try a different search term or adjust your filters",
      style = MaterialTheme.typography.bodyMedium,
      color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
  }
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun SearchHistoryHeader(onClearHistory: () -> Unit, modifier: Modifier = Modifier) {
  Row(
    modifier = modifier.fillMaxWidth().padding(horizontal = 16.dp),
    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
  ) {
    Text(
      text = "Recent Searches",
      style = MaterialTheme.typography.labelLarge,
      color = MaterialTheme.colorScheme.primary,
    )
    Spacer(modifier = Modifier.weight(1f))
    IconButton(
      content = {
        Icon(imageVector = Icons.Default.ClearAll, contentDescription = "Clear History")
      },
      onClick = onClearHistory,
      enabled = true,
      modifier = Modifier.defaultMinSize(minHeight = 8.dp),
    )
  }
}
