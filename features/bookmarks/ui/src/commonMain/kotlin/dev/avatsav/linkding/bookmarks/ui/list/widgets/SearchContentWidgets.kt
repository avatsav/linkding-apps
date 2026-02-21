package dev.avatsav.linkding.bookmarks.ui.list.widgets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ClearAll
import androidx.compose.material.icons.filled.History
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
import dev.avatsav.linkding.data.model.SearchHistory
import linkding_apps.features.bookmarks.ui.generated.resources.Res
import linkding_apps.features.bookmarks.ui.generated.resources.search_clear_history
import linkding_apps.features.bookmarks.ui.generated.resources.search_empty_description
import linkding_apps.features.bookmarks.ui.generated.resources.search_empty_title
import linkding_apps.features.bookmarks.ui.generated.resources.search_history
import linkding_apps.features.bookmarks.ui.generated.resources.search_recent
import org.jetbrains.compose.resources.stringResource

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
    onClick = onClick,
  ) {
    ListItem(
      modifier = Modifier.defaultMinSize(minHeight = 36.dp),
      headlineContent = {
        Text(
          text = searchHistory.query,
          style = MaterialTheme.typography.bodyMedium,
          maxLines = 1,
          overflow = TextOverflow.Ellipsis,
        )
      },
      leadingContent = {
        Icon(
          imageVector = Icons.Default.History,
          contentDescription = stringResource(Res.string.search_history),
        )
      },
    )
  }
}

@Composable
fun EmptySearchResults(modifier: Modifier = Modifier) {
  Column(
    modifier = modifier.fillMaxWidth().padding(vertical = 32.dp, horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(8.dp),
  ) {
    Text(
      text = stringResource(Res.string.search_empty_title),
      style = MaterialTheme.typography.titleMedium,
    )
    Text(
      text = stringResource(Res.string.search_empty_description),
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
      text = stringResource(Res.string.search_recent),
      style = MaterialTheme.typography.titleSmall,
      color = MaterialTheme.colorScheme.primary,
    )
    Spacer(modifier = Modifier.weight(1f))
    IconButton(
      content = {
        Icon(
          imageVector = Icons.Default.ClearAll,
          contentDescription = stringResource(Res.string.search_clear_history),
        )
      },
      onClick = onClearHistory,
      enabled = true,
      modifier = Modifier.defaultMinSize(minHeight = 8.dp),
    )
  }
}
