package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
  primary = EmeraldLight,
  onPrimary = EmeraldDark,
  primaryContainer = EmeraldDark,
  onPrimaryContainer = EmeraldSubtle,
  secondary = GoldPrimary,
  onSecondary = Color(0xFF2C2000),
  secondaryContainer = GoldDark,
  onSecondaryContainer = GoldLight,
  background = BackgroundDark,
  onBackground = TextPrimaryDark,
  surface = SurfaceDark,
  onSurface = TextPrimaryDark,
  surfaceVariant = SurfaceCardDark,
  onSurfaceVariant = TextSecondaryDark,
  outline = OutlineDark
)

private val LightColorScheme = lightColorScheme(
  primary = EmeraldPrimary,
  onPrimary = Color.White,
  primaryContainer = EmeraldSubtle,
  onPrimaryContainer = EmeraldDark,
  secondary = GoldDark,
  onSecondary = Color.White,
  secondaryContainer = GoldLight,
  onSecondaryContainer = Color(0xFF332000),
  background = SurfaceLight,
  onBackground = TextPrimaryLight,
  surface = SurfaceCardLight,
  onSurface = TextPrimaryLight,
  surfaceVariant = Color(0xFFF0F4F2),
  onSurfaceVariant = TextSecondaryLight,
  outline = OutlineLight
)

@Composable
fun FriendshipZoneTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  content: @Composable () -> Unit
) {
  val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

  MaterialTheme(
    colorScheme = colorScheme,
    typography = AppTypography,
    content = content
  )
}
