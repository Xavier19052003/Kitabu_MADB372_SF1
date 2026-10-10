package com.example.kitabu_madb372_sf1.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val KitabuColorScheme = lightColorScheme(
    primary = KitabuPurple,
    onPrimary = KitabuCard,

    primaryContainer = KitabuLavender,
    onPrimaryContainer = KitabuDarkText,

    secondary = KitabuYellow,
    onSecondary = KitabuDarkText,

    background = KitabuBackground,
    onBackground = KitabuDarkText,

    surface = KitabuCard,
    onSurface = KitabuDarkText,

    surfaceVariant = KitabuNavBackground,
    onSurfaceVariant = KitabuMutedText,

    outline = KitabuOutline
)

@Composable
fun Kitabu_MADB372_SF1Theme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = KitabuColorScheme,
        typography = Typography,
        content = content
    )
}