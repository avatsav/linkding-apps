package dev.avatsav.linkding.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable

@Composable
internal actual fun colorScheme(
    darkTheme: Boolean,
    dynamicColor: Boolean,
): ColorScheme = when {
    darkTheme -> LinkdingDarkColorScheme
    else -> LinkdingLightColorScheme
}
