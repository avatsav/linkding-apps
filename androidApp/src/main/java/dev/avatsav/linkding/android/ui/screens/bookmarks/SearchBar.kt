package dev.avatsav.linkding.android.ui.screens.bookmarks

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ElevatedAssistChip
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.surfaceColorAtElevation
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.android.R
import dev.avatsav.linkding.android.ui.theme.LinkdingTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    modifier: Modifier = Modifier,
    searchClicked: () -> Unit,
    menuClicked: () -> Unit,
    tagsClicked: () -> Unit,
) {
    Column {
        BasicTextField(value = "",
            enabled = false,
            onValueChange = {},
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .height(48.dp)
                .background(
                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
                    shape = RoundedCornerShape(50)
                )
                .fillMaxWidth(),
            singleLine = true,
            decorationBox = { innerTextField ->
                Row(
                    modifier = modifier, verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = "Search"
                        )
                    }
                    Box(Modifier.weight(1f)) {
                        Text(
                            "Search for words or #tags", style = LocalTextStyle.current.copy(
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        )
                        innerTextField()
                    }
                    IconButton(onClick = { /*TODO*/ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            tint = MaterialTheme.colorScheme.onSurface,
                            contentDescription = "Menu"
                        )
                    }
                }
            })
        LazyRow(
            contentPadding = PaddingValues(start = 12.dp)
        ) {
            item(key = "tag") {
                ElevatedAssistChip(
                    modifier = Modifier.padding(start = 4.dp),
                    label = { Text("Tags") },
                    trailingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_arrow_drop_down_20),
                            contentDescription = "Tags",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    },
                    onClick = { tagsClicked() },
                )
            }
            item(key = "filter-group") {
                ElevatedFilterChip(
                    modifier = Modifier.padding(start = 4.dp),
                    label = { Text("Unread") },
                    onClick = { /*TODO*/ },
                    selected = false,
                )
                ElevatedFilterChip(
                    modifier = Modifier.padding(start = 4.dp),
                    label = { Text("Archived") },
                    onClick = { /*TODO*/ },
                    selected = false,
                )
            }
        }
    }

}

@Preview
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun SearchBar_Preview() {
    LinkdingTheme {
        Surface {
            SearchBar(searchClicked = {}, menuClicked = {}, tagsClicked = {})
        }
    }
}