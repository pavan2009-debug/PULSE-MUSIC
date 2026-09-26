package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import com.example.model.GlassEffect
import com.example.model.ThemeMode

val LocalGlassEffect = compositionLocalOf { GlassEffect.FROSTED_GLASS }
val LocalAccentColor = compositionLocalOf { SpotifyGreen }

@Composable
fun MyApplicationTheme(
    themeMode: ThemeMode = ThemeMode.DARK,
    glassEffect: GlassEffect = GlassEffect.FROSTED_GLASS,
    accentColor: Color = SpotifyGreen,
    content: @Composable () -> Unit
) {
    val isDark = when (themeMode) {
        ThemeMode.DARK -> true
        ThemeMode.LIGHT -> false
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
    }

    val colorScheme = if (isDark) {
        createDarkColorScheme(accentColor)
    } else {
        createLightColorScheme(accentColor)
    }

    CompositionLocalProvider(
        LocalGlassEffect provides glassEffect,
        LocalAccentColor provides accentColor
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}
