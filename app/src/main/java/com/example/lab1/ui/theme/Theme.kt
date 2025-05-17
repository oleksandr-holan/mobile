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

private val DarkBrownColorScheme = darkColorScheme(
    primary = BrownPrimaryDark,
    onPrimary = TextOnPrimaryBrown,
    primaryContainer = Color(0xFF4E342E),
    onPrimaryContainer = TextPrimaryDark,

    secondary = DeepOrangeAccent,
    onSecondary = TextOnOrange,
    secondaryContainer = Color(0xFFBF360C),
    onSecondaryContainer = TextPrimaryDark,

    tertiary = BrownPrimaryLight,
    onTertiary = TextPrimaryLight,
    tertiaryContainer = Color(0xFF5D4037),
    onTertiaryContainer = TextPrimaryDark,

    background = BackgroundDark,
    onBackground = TextPrimaryDark,

    surface = BackgroundDarkElevated,
    onSurface = TextPrimaryDark,
    surfaceVariant = Color(0xFF4A3B35),
    onSurfaceVariant = TextSecondaryDark,

    error = ErrorRed,
    onError = OnError,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),

    outline = BrownPrimaryLight
)

private val LightBrownColorScheme = lightColorScheme(
    primary = BrownPrimary,
    onPrimary = TextOnPrimaryBrown,
    primaryContainer = BrownPrimaryLight,
    onPrimaryContainer = TextPrimaryLight,

    secondary = OrangeAccent,
    onSecondary = TextOnOrange,
    secondaryContainer = Color(0xFFFFCC80),
    onSecondaryContainer = TextPrimaryLight,

    tertiary = CreamAccent,
    onTertiary = TextOnCream,
    tertiaryContainer = Color(0xFFFFF5DD),
    onTertiaryContainer = TextPrimaryLight,

    background = BackgroundLight,
    onBackground = TextPrimaryLight,

    surface = BackgroundLightElevated,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFEFEBE9),
    onSurfaceVariant = TextSecondaryLight,

    error = ErrorRed,
    onError = OnError,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002),

    outline = BrownPrimaryDark
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

        darkTheme -> DarkBrownColorScheme
        else -> LightBrownColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}