package com.example.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

val SpotifyBlack = Color(0xFF121212)
val SpotifyDarkCharcoal = Color(0xFF181818)
val SpotifyCardGray = Color(0xFF242424)
val SpotifyBorderGray = Color(0xFF383838)
val SpotifyGreen = Color(0xFF1DB954)
val SpotifyLightGreen = Color(0xFF1ED760)
val SpotifyMint = Color(0xFF10B981)
val SpotifyCyan = Color(0xFF00E5FF)
val SpotifyPurple = Color(0xFF8B5CF6)
val SpotifyOrange = Color(0xFFF97316)

val TextPrimaryDark = Color(0xFFFFFFFF)
val TextSecondaryDark = Color(0xFFB3B3B3)
val TextTertiaryDark = Color(0xFF71717A)

val TextPrimaryLight = Color(0xFF0F172A)
val TextSecondaryLight = Color(0xFF475569)
val TextTertiaryLight = Color(0xFF94A3B8)

// Compatibility aliases
val TextPrimary = TextPrimaryDark
val TextSecondary = TextSecondaryDark
val TextTertiary = TextTertiaryDark

fun createDarkColorScheme(accentColor: Color = SpotifyGreen) = darkColorScheme(
    primary = accentColor,
    onPrimary = Color.Black,
    primaryContainer = accentColor.copy(alpha = 0.2f),
    onPrimaryContainer = Color.White,
    secondary = SpotifyMint,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF064E3B),
    onSecondaryContainer = Color(0xFFA7F3D0),
    background = SpotifyBlack,
    onBackground = TextPrimaryDark,
    surface = SpotifyDarkCharcoal,
    onSurface = TextPrimaryDark,
    surfaceVariant = SpotifyCardGray,
    onSurfaceVariant = TextSecondaryDark,
    outline = SpotifyBorderGray
)

fun createLightColorScheme(accentColor: Color = SpotifyGreen) = lightColorScheme(
    primary = accentColor,
    onPrimary = Color.White,
    primaryContainer = accentColor.copy(alpha = 0.15f),
    onPrimaryContainer = accentColor,
    secondary = SpotifyMint,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD1FAE5),
    onSecondaryContainer = Color(0xFF065F46),
    background = Color(0xFFF8FAFC),
    onBackground = TextPrimaryLight,
    surface = Color(0xFFFFFFFF),
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFFE2E8F0)
)

val DarkColorScheme = createDarkColorScheme()
val LightColorScheme = createLightColorScheme()
