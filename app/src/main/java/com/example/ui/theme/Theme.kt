package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = PrimaryGreen,
    onPrimary = Color.White,
    primaryContainer = PrimaryGreenLight,
    onPrimaryContainer = PrimaryGreenDark,
    secondary = SecondaryTeal,
    onSecondary = Color.White,
    secondaryContainer = SecondaryTealLight,
    onSecondaryContainer = Color(0xFF001F25),
    tertiary = AccentGold,
    onTertiary = Color.White,
    tertiaryContainer = AccentGoldLight,
    onTertiaryContainer = Color(0xFF281900),
    error = AlertRed,
    errorContainer = AlertRedLight,
    onError = Color.White,
    background = BackgroundLight,
    onBackground = TextPrimary,
    surface = SurfaceLight,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = TextSecondary,
    outline = Color(0xFF717971)
)

private val DarkColorScheme = darkColorScheme(
    primary = PrimaryGreenNight,
    onPrimary = Color(0xFF003822),
    secondary = SecondaryTealLight,
    onSecondary = Color(0xFF00363F),
    tertiary = AccentGoldLight,
    onTertiary = Color(0xFF452C00),
    background = BackgroundNight,
    onBackground = Color(0xFFE1E4DF),
    surface = SurfaceNight,
    onSurface = Color(0xFFE1E4DF)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = false, // Force consistent high-contrast Pakistani institutional light theme
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
