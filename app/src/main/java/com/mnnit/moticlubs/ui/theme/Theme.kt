package com.mnnit.moticlubs.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.google.accompanist.systemuicontroller.rememberSystemUiController

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
fun getColorScheme() =
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (isSystemInDarkTheme()) dynamicDarkColorScheme(LocalContext.current) else dynamicLightColorScheme(
            LocalContext.current
        )
    } else {
        if (isSystemInDarkTheme()) DarkColorPalette else LightColorPalette
    }

@Composable
fun SetNavBarsTheme(elevation: Dp = 0.dp) {
    val colorScheme = getColorScheme()
    val darkTheme = isSystemInDarkTheme()
    val systemUiController = rememberSystemUiController()
    DisposableEffect(systemUiController, darkTheme) {
        systemUiController.setSystemBarsColor(
            color = if (elevation == 0.dp) colorScheme.background else colorScheme.surfaceColorAtElevation(2.dp),
            darkIcons = !darkTheme
        )
        onDispose { }
    }
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
