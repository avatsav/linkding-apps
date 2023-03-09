import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsBottomSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheet(
        modifier = modifier,
        onDismissRequest = onDismissRequest,
    ) {
        TopAppBar(
            title = { Text(text = "Tags") },
        )
        LazyColumn {
            items(20) {
                ListItem(
                    headlineContent = { Text("Item $it") },
                    leadingContent = {
                        Icon(
                            Icons.Default.Favorite,
                            contentDescription = "Localized description",
                        )
                    },
                )
            }
        }
    }
}


