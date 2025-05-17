package com.example.lab1.ui.theme

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


private val DarkColorScheme = darkColorScheme(
    primary = AppPrimaryDarkBlue,
    onPrimary = AppTextOnPrimary,
    primaryContainer = Color(0xFF004A8F),
    onPrimaryContainer = Color(0xFFD2E4FF),

    secondary = AppSecondaryGreen,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF003912),
    onSecondaryContainer = Color(0xFF97F79D),

    tertiary = AppAccentPink,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFF99003A),
    onTertiaryContainer = Color(0xFFFFD9E0),

    background = AppBackgroundDark,
    onBackground = AppTextOnBackgroundDark,

    surface = AppBackgroundDarkElevated,
    onSurface = AppTextOnBackgroundDark,
    surfaceVariant = Color(0xFF303030),
    onSurfaceVariant = Color(0xFFB0B0B0),

    error = AppErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    outline = Color(0xFF899388)
)

private val LightColorScheme = lightColorScheme(
    primary = AppPrimaryBlue,
    onPrimary = AppTextOnPrimary,
    primaryContainer = Color(0xFFD2E4FF),
    onPrimaryContainer = Color(0xFF001C3B),

    secondary = AppSecondaryGreen,
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF97F79D),
    onSecondaryContainer = Color(0xFF002105),

    tertiary = AppAccentPink,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFD9E0),
    onTertiaryContainer = Color(0xFF3E001D),

    background = AppBackgroundLight,
    onBackground = AppTextOnBackgroundLight,

    surface = AppBackgroundLightElevated,
    onSurface = AppTextOnBackgroundLight,
    surfaceVariant = Color(0xFFE0E0E0),
    onSurfaceVariant = Color(0xFF424242),

    error = AppErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    outline = Color(0xFF73796F)
)

@Composable
fun Lab1Theme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}