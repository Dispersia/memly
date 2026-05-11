package io.dispersia.memly.core.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import io.dispersia.memly.core.presentation.*

private val DarkColorScheme = darkColorScheme(
    primary = Indigo80,
    onPrimary = Color(0xFF002984),
    primaryContainer = Color(0xFF303F9F),
    onPrimaryContainer = Color(0xFFDAE2FF),
    secondary = IndigoGrey80,
    secondaryContainer = Color(0xFF4A4D60),
    tertiary = Teal80,
    tertiaryContainer = Color(0xFF00695C),
    onTertiaryContainer = Color(0xFFB2DFDB),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF93000A),
    surface = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF49454F),
    onSurfaceVariant = Color(0xFFCAC4D0),
    surfaceContainerLowest = Color(0xFF1C1B1F),
    surfaceContainerLow = Color(0xFF252429),
    surfaceContainer = Color(0xFF2B2930),
    surfaceContainerHigh = Color(0xFF36343B),
    surfaceContainerHighest = Color(0xFF413F46),
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),
)

private val LightColorScheme = lightColorScheme(
    primary = Indigo40,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFDDE1FF),
    onPrimaryContainer = Color(0xFF001A6E),
    secondary = IndigoGrey40,
    secondaryContainer = Color(0xFFDDE0F9),
    tertiary = Teal40,
    tertiaryContainer = Color(0xFFA7F5EC),
    error = Color(0xFFBA1A1A),
    errorContainer = Color(0xFFFFDAD6),
    surface = Color(0xFFFBF8FF),
    onSurface = Color(0xFF1B1B1F),
    surfaceVariant = Color(0xFFE2E1EC),
    onSurfaceVariant = Color(0xFF45464F),
)

@Composable
fun MemlyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val memlyColorScheme = when {
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    MaterialTheme(
        colorScheme = memlyColorScheme,
        content = content
    )
}
