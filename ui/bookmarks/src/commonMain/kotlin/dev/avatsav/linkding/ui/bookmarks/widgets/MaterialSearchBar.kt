package dev.avatsav.linkding.ui.bookmarks.widgets // package dev.avatsav.linkding.ui.bookmarks
//
// import android.content.res.Configuration
// import androidx.compose.animation.Crossfade
// import androidx.compose.foundation.layout.Box
// import androidx.compose.foundation.layout.fillMaxWidth
// import androidx.compose.foundation.layout.padding
// import androidx.compose.foundation.lazy.LazyColumn
// import androidx.compose.material.icons.Icons
// import androidx.compose.material.icons.filled.ArrowBack
// import androidx.compose.material.icons.filled.MoreVert
// import androidx.compose.material.icons.filled.Search
// import androidx.compose.material3.Divider
// import androidx.compose.material3.ExperimentalMaterial3Api
// import androidx.compose.material3.Icon
// import androidx.compose.material3.IconButton
// import androidx.compose.material3.MaterialTheme
// import androidx.compose.material3.Surface
// import androidx.compose.material3.Text
// import androidx.compose.runtime.Composable
// import androidx.compose.runtime.getValue
// import androidx.compose.runtime.mutableStateOf
// import androidx.compose.runtime.saveable.rememberSaveable
// import androidx.compose.runtime.setValue
// import androidx.compose.ui.Alignment
// import androidx.compose.ui.Modifier
// import androidx.compose.ui.tooling.preview.Preview
// import androidx.compose.ui.unit.dp
// import dev.avatsav.linkding.android.theme.LinkdingTheme
//
// @OptIn(ExperimentalMaterial3Api::class)
// @Composable
// fun MaterialSearchBar(
//    onQueryChange: (String) -> Unit,
// ) {
//    var query by rememberSaveable { mutableStateOf("") }
//    var active by rememberSaveable { mutableStateOf(false) }
//
//    Box {
//        androidx.compose.material3.SearchBar(
//            modifier = Modifier
//                .align(Alignment.TopCenter)
//                .fillMaxWidth()
//                .padding(12.dp),
//            query = query,
//            onQueryChange = { queryString ->
//                query = queryString
//                onQueryChange(queryString)
//            },
//            active = active,
//            onActiveChange = {
//                active = it
//            },
//            onSearch = {},
//            leadingIcon = {
//                IconButton(onClick = {
//                    active = !active
//                }) {
//                    Crossfade(
//                        targetState = active,
//                    ) { isActive ->
//                        if (isActive) {
//                            Icon(
//                                imageVector = Icons.Default.ArrowBack,
//                                contentDescription = "Back",
//                            )
//                        } else {
//                            Icon(
//                                imageVector = Icons.Default.Search,
//                                contentDescription = "Search",
//                            )
//                        }
//                    }
//                }
//            },
//            trailingIcon = {
//                if (!active) {
//                    IconButton(onClick = { active = false }) {
//                        Icon(
//                            imageVector = Icons.Default.MoreVert,
//                            tint = MaterialTheme.colorScheme.onSurface,
//                            contentDescription = "Search",
//                        )
//                    }
//                }
//            },
//            placeholder = {
//                Text("Search for words or #tags")
//            },
//        ) {
//            LazyColumn {
//                items(200) {
//                    Text(text = it.toString(), modifier = Modifier.padding(12.dp))
//                    Divider()
//                }
//            }
//        }
//    }
// }
//
// @Preview
// @Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
// @Composable
// fun Search_Preview() {
//    LinkdingTheme {
//        Surface {
//            MaterialSearchBar(onQueryChange = {})
//        }
//    }
// }
