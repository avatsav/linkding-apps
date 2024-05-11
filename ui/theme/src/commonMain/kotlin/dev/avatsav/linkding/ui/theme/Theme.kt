package dev.avatsav.linkding.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Composable
fun LinkdingTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColors: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = colorScheme(darkTheme, dynamicColors),
        typography = LinkdingTypography,
        content = content,
    )
}
