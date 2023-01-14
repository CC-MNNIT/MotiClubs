package com.mnnit.moticlubs.ui.theme

import android.content.Context
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorPalette = darkColorScheme(
    background = Cultured,
    onBackground = OxfordBlue,
    primary = OxfordBlue,
    onPrimary = Color.White,
    primaryContainer = Blue,
    onPrimaryContainer = Color.White,
    secondaryContainer = Color.White,
    onSecondaryContainer = Color.Black,
    tertiaryContainer = MaximumYellowRed,
    onTertiaryContainer = OxfordBlue
)

private val LightColorPalette = lightColorScheme(
    background = Cultured,
    onBackground = OxfordBlue,
    primary = OxfordBlue,
    onPrimary = Color.White,
    primaryContainer = Blue,
    onPrimaryContainer = Color.White,
    secondaryContainer = Color.White,
    onSecondaryContainer = Color.Black,
    tertiaryContainer = MaximumYellowRed,
    onTertiaryContainer = OxfordBlue
)

@Composable
fun getColorScheme(context: Context) =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (isSystemInDarkTheme()) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (isSystemInDarkTheme()) DarkColorPalette else LightColorPalette
    }


@Composable
fun MotiClubsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorPalette else LightColorPalette
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes(),
        content = content
    )
}

@Composable
fun MotiClubsTheme(
    colorScheme: ColorScheme,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = Shapes(),
        content = content
    )
}
