// package dev.avatsav.linkding.ui.bookmarks
//
// import androidx.compose.foundation.background
// import androidx.compose.foundation.layout.Box
// import androidx.compose.foundation.layout.Column
// import androidx.compose.foundation.layout.PaddingValues
// import androidx.compose.foundation.layout.Row
// import androidx.compose.foundation.layout.fillMaxWidth
// import androidx.compose.foundation.layout.height
// import androidx.compose.foundation.layout.padding
// import androidx.compose.foundation.lazy.LazyRow
// import androidx.compose.foundation.shape.RoundedCornerShape
// import androidx.compose.foundation.text.BasicTextField
// import androidx.compose.material.icons.Icons
// import androidx.compose.material.icons.filled.MoreVert
// import androidx.compose.material.icons.filled.Search
// import androidx.compose.material3.AssistChip
// import androidx.compose.material3.AssistChipDefaults
// import androidx.compose.material3.ExperimentalMaterial3Api
// import androidx.compose.material3.FilterChip
// import androidx.compose.material3.FilterChipDefaults
// import androidx.compose.material3.Icon
// import androidx.compose.material3.IconButton
// import androidx.compose.material3.LocalTextStyle
// import androidx.compose.material3.MaterialTheme
// import androidx.compose.material3.Surface
// import androidx.compose.material3.Text
// import androidx.compose.material3.surfaceColorAtElevation
// import androidx.compose.runtime.Composable
// import androidx.compose.ui.Alignment
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.res.painterResource
// import androidx.compose.ui.tooling.preview.Preview
// import androidx.compose.ui.unit.dp
// import dev.avatsav.linkding.android.R
// import dev.avatsav.linkding.android.theme.LinkdingTheme
// import dev.avatsav.linkding.ui.bookmarks.SearchState
// import dev.avatsav.linkding.ui.bookmarks.vm.SearchState
//
// @OptIn(ExperimentalMaterial3Api::class)
// @Composable
// fun SearchBar(
//    modifier: Modifier = Modifier,
//    searchState: SearchState,
//    searchClicked: () -> Unit,
//    menuClicked: () -> Unit,
//    tagsClicked: () -> Unit,
//    archivedFilter: (Boolean) -> Unit,
// ) {
//    Column {
//        BasicTextField(
//            value = "",
//            enabled = false,
//            onValueChange = {},
//            modifier = Modifier
//                .padding(horizontal = 12.dp, vertical = 8.dp)
//                .height(48.dp)
//                .background(
//                    color = MaterialTheme.colorScheme.surfaceColorAtElevation(6.dp),
//                    shape = RoundedCornerShape(50),
//                )
//                .fillMaxWidth(),
//            singleLine = true,
//            decorationBox = { innerTextField ->
//                Row(
//                    modifier = modifier,
//                    verticalAlignment = Alignment.CenterVertically,
//                ) {
//                    IconButton(onClick = { /*TODO*/ }) {
//                        Icon(
//                            imageVector = Icons.Default.Search,
//                            tint = MaterialTheme.colorScheme.onSurface,
//                            contentDescription = "Search",
//                        )
//                    }
//                    Box(Modifier.weight(1f)) {
//                        Text(
//                            "Search for words or #tags",
//                            style = LocalTextStyle.current.copy(
//                                color = MaterialTheme.colorScheme.onSurface,
//                            ),
//                        )
//                        innerTextField()
//                    }
//                    IconButton(onClick = { /*TODO*/ }) {
//                        Icon(
//                            imageVector = Icons.Default.MoreVert,
//                            tint = MaterialTheme.colorScheme.onSurface,
//                            contentDescription = "Menu",
//                        )
//                    }
//                }
//            },
//        )
//        LazyRow(
//            contentPadding = PaddingValues(start = 12.dp),
//        ) {
//            item(key = "tags") {
//                AssistChip(
//                    modifier = Modifier.padding(start = 4.dp),
//                    label = { Text("Tags") },
//                    trailingIcon = {
//                        Icon(
//                            painter = painterResource(id = R.drawable.ic_arrow_drop_down_20),
//                            contentDescription = "Tags",
//                            tint = MaterialTheme.colorScheme.onSurface,
//                        )
//                    },
//                    onClick = { tagsClicked() },
//                    border = AssistChipDefaults.assistChipBorder(borderWidth = 0.5.dp),
//                )
//            }
//            item(key = "archiveFilter") {
//                FilterChip(
//                    modifier = Modifier.padding(start = 8.dp),
//                    label = { Text("Archived") },
//                    selected = searchState.archivedFilterSelected,
//                    onClick = {
//                        val newValue = !searchState.archivedFilterSelected
//                        archivedFilter(newValue)
//                    },
//                    border = FilterChipDefaults.filterChipBorder(borderWidth = 0.5.dp),
//                )
//            }
//        }
//    }
// }
//
// @Preview
// @Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
// @Composable
// fun SearchBar_Preview() {
//    LinkdingTheme {
//        Surface {
//            SearchBar(
//                searchState = SearchState(
//                    query = "Staff",
//                    archivedFilterSelected = false,
//                ),
//                searchClicked = {},
//                menuClicked = {},
//                tagsClicked = {},
//                archivedFilter = {},
//            )
//        }
//    }
// }
