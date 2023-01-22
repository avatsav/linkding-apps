package dev.avatsav.linkding.android.ui.screens.bookmarks

import android.content.res.Configuration
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import dev.avatsav.linkding.android.ui.components.SmallCircularProgressIndicator
import dev.avatsav.linkding.android.ui.theme.LinkdingTheme

@Composable
fun LoadingMoreItem(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .padding(16.dp)
            .height(56.dp)
            .fillMaxWidth(),
    ) {
        SmallCircularProgressIndicator(
            modifier.align(Alignment.Center)
        )
    }
}

@Preview(showBackground = true)
@Preview(uiMode = Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoadingMoreItem_Preview() {
    LinkdingTheme {
        Surface {
            LoadingMoreItem()
        }
    }
}