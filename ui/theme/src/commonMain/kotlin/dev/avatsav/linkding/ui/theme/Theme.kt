package dev.avatsav.linkding.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LinkdingTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  dynamicColors: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialExpressiveTheme(
    colorScheme = colorScheme(darkTheme, dynamicColors),
    typography = LinkdingTypography,
    content = content,
  )
}
