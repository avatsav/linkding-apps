package dev.avatsav.linkding.android.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SmallCircularProgressIndicator() {
    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 3.dp)
}