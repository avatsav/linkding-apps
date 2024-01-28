//package dev.avatsav.linkding.ui.bookmarks
//
//import androidx.compose.foundation.layout.PaddingValues
//import androidx.compose.foundation.layout.fillMaxWidth
//import androidx.compose.foundation.lazy.LazyColumn
//import androidx.compose.foundation.lazy.items
//import androidx.compose.foundation.lazy.rememberLazyListState
//import androidx.compose.material3.Divider
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.ListItem
//import androidx.compose.material3.ModalBottomSheet
//import androidx.compose.material3.Text
//import androidx.compose.material3.TopAppBar
//import androidx.compose.material3.rememberModalBottomSheetState
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.getValue
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.unit.dp
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
//import dev.avatsav.linkding.android.extensions.OnEndReached
//import dev.avatsav.linkding.domain.Tag
//import dev.avatsav.linkding.ui.PageStatus
//import dev.avatsav.linkding.ui.bookmarks.TagsViewModel
//import dev.avatsav.linkding.ui.onPagedContent
//import org.koin.androidx.compose.koinViewModel
//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun TagsBottomSheet(
//    onDismissRequest: () -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    val viewModel: TagsViewModel = koinViewModel()
//
//    val state by viewModel.state.collectAsStateWithLifecycle()
//    val listState = rememberLazyListState()
//    val bottomSheetState = rememberModalBottomSheetState()
//    var contentStatus = PageStatus.HasMore
//    var tags = emptyList<Tag>()
//
//    state.tagsState onPagedContent { pagedContent ->
//        contentStatus = pagedContent.status
//        tags = pagedContent.value
//    }
//
//    ModalBottomSheet(
//        modifier = modifier,
//        sheetState = bottomSheetState,
//        onDismissRequest = onDismissRequest,
//    ) {
//        TopAppBar(title = { Text(text = "Tags") })
//        Divider(thickness = 0.5.dp)
//        LazyColumn(
//            modifier = Modifier.fillMaxWidth(),
//            state = listState,
//            contentPadding = PaddingValues(bottom = 24.dp),
//        ) {
//            items(items = tags, key = { it.id }) { tag ->
//                ListItem(
//                    headlineContent = { Text(tag.name) },
//                    leadingContent = { Text("#") },
//                )
//            }
//            if (contentStatus == PageStatus.HasMore || contentStatus == PageStatus.LoadingMore) {
//                item {
//                    LoadingMoreItem()
//                }
//            }
//        }
//        listState.OnEndReached { viewModel.loadMore() }
//    }
//}
