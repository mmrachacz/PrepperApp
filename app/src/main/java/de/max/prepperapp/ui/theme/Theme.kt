package de.max.prepperapp.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val PrepperLightColorScheme = lightColorScheme(
    primary = PrepperLightPrimary,
    onPrimary = PrepperLightOnPrimary,
    primaryContainer = PrepperLightPrimaryContainer,
    onPrimaryContainer = PrepperLightOnPrimaryContainer,
    secondary = PrepperLightSecondary,
    onSecondary = PrepperLightOnSecondary,
    secondaryContainer = PrepperLightSecondaryContainer,
    onSecondaryContainer = PrepperLightOnSecondaryContainer,
    tertiary = PrepperLightTertiary,
    onTertiary = PrepperLightOnTertiary,
    tertiaryContainer = PrepperLightTertiaryContainer,
    onTertiaryContainer = PrepperLightOnTertiaryContainer,
    background = PrepperLightBackground,
    onBackground = PrepperLightOnBackground,
    surface = PrepperLightSurface,
    onSurface = PrepperLightOnSurface,
    surfaceVariant = PrepperLightSurfaceVariant,
    onSurfaceVariant = PrepperLightOnSurfaceVariant,
    outline = PrepperLightOutline
)

private val PrepperDarkColorScheme = darkColorScheme(
    primary = PrepperDarkPrimary,
    onPrimary = PrepperDarkOnPrimary,
    primaryContainer = PrepperDarkPrimaryContainer,
    onPrimaryContainer = PrepperDarkOnPrimaryContainer,
    secondary = PrepperDarkSecondary,
    onSecondary = PrepperDarkOnSecondary,
    secondaryContainer = PrepperDarkSecondaryContainer,
    onSecondaryContainer = PrepperDarkOnSecondaryContainer,
    tertiary = PrepperDarkTertiary,
    onTertiary = PrepperDarkOnTertiary,
    tertiaryContainer = PrepperDarkTertiaryContainer,
    onTertiaryContainer = PrepperDarkOnTertiaryContainer,
    background = PrepperDarkBackground,
    onBackground = PrepperDarkOnBackground,
    surface = PrepperDarkSurface,
    onSurface = PrepperDarkOnSurface,
    surfaceVariant = PrepperDarkSurfaceVariant,
    onSurfaceVariant = PrepperDarkOnSurfaceVariant,
    outline = PrepperDarkOutline
)

private val PrepperShapes = Shapes(
    extraSmall = RoundedCornerShape(10.dp),
    small = RoundedCornerShape(14.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(26.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

@Composable
fun PrepperAppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        PrepperDarkColorScheme
    } else {
        PrepperLightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = PrepperShapes,
        content = content
    )
}