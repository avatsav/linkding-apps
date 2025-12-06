package dev.avatsav.linkding.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import linkding_apps.ui.theme.generated.resources.GoogleSansFlex_var
import linkding_apps.ui.theme.generated.resources.Res
import org.jetbrains.compose.resources.Font

@Composable
fun googleSansFlexFontFamily() =
  FontFamily(
    Font(
      resource = Res.font.GoogleSansFlex_var,
      weight = FontWeight.Normal,
      variationSettings =
        FontVariation.Settings(
          FontVariation.weight(FontWeight.Normal.weight),
          FontVariation.width(100f),
        ),
    ),
    Font(
      resource = Res.font.GoogleSansFlex_var,
      weight = FontWeight.Medium,
      variationSettings =
        FontVariation.Settings(
          FontVariation.weight(FontWeight.Medium.weight),
          FontVariation.width(100f),
        ),
    ),
    Font(
      resource = Res.font.GoogleSansFlex_var,
      weight = FontWeight.Bold,
      variationSettings =
        FontVariation.Settings(
          FontVariation.weight(FontWeight.Bold.weight),
          FontVariation.width(100f),
        ),
    ),
  )

val LinkdingTypography
  @Composable
  get() =
    Typography(
      displayLarge =
        TextStyle(
          fontFamily = googleSansFlexFontFamily(),
          fontWeight = FontWeight.Normal,
          fontSize = 57.sp,
          lineHeight = 64.sp,
          letterSpacing = (-0.25).sp,
        ),
      displayMedium =
        TextStyle(
          fontFamily = googleSansFlexFontFamily(),
          fontWeight = FontWeight.Normal,
          fontSize = 45.sp,
          lineHeight = 52.sp,
          letterSpacing = 0.sp,
        ),
      displaySmall =
        TextStyle(
          fontFamily = googleSansFlexFontFamily(),
          fontWeight = FontWeight.Normal,
          fontSize = 36.sp,
          lineHeight = 44.sp,
          letterSpacing = 0.sp,
        ),
      headlineLarge =
        TextStyle(
          fontFamily = googleSansFlexFontFamily(),
          fontWeight = FontWeight.Normal,
          fontSize = 32.sp,
          lineHeight = 40.sp,
          letterSpacing = 0.sp,
        ),
      headlineMedium =
        TextStyle(
          fontFamily = googleSansFlexFontFamily(),
          fontWeight = FontWeight.Normal,
          fontSize = 28.sp,
          lineHeight = 36.sp,
          letterSpacing = 0.sp,
        ),
      headlineSmall =
        TextStyle(
          fontFamily = googleSansFlexFontFamily(),
          fontWeight = FontWeight.Normal,
          fontSize = 24.sp,
          lineHeight = 32.sp,
          letterSpacing = 0.sp,
        ),
      titleLarge =
        TextStyle(
          fontFamily = googleSansFlexFontFamily(),
          fontWeight = FontWeight.Normal,
          fontSize = 22.sp,
          lineHeight = 28.sp,
          letterSpacing = 0.sp,
        ),
      titleMedium =
        TextStyle(
          fontFamily = googleSansFlexFontFamily(),
          fontWeight = FontWeight.Medium,
          fontSize = 16.sp,
          lineHeight = 24.sp,
          letterSpacing = 0.15.sp,
        ),
      titleSmall =
        TextStyle(
          fontFamily = googleSansFlexFontFamily(),
          fontWeight = FontWeight.Medium,
          fontSize = 14.sp,
          lineHeight = 20.sp,
          letterSpacing = 0.1.sp,
        ),
      bodyLarge =
        TextStyle(
          fontFamily = FontFamily.Default,
          fontWeight = FontWeight.Normal,
          fontSize = 16.sp,
          lineHeight = 24.sp,
          letterSpacing = 0.5.sp,
        ),
    )
