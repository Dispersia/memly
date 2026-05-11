package io.dispersia.memlywear.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme

private val Indigo80 = Color(0xFFB4C5FF)
private val Teal80 = Color(0xFF82D9D0)

private val WearColorScheme = ColorScheme(
    primary = Indigo80,
    onPrimary = Color(0xFF002984),
    primaryContainer = Color(0xFF303F9F),
    onPrimaryContainer = Color(0xFFDAE2FF),
    secondary = Color(0xFFC3C5D9),
    onSecondary = Color(0xFF2D2F42),
    secondaryContainer = Color(0xFF4A4D60),
    onSecondaryContainer = Color(0xFFDDE0F9),
    tertiary = Teal80,
    onTertiary = Color(0xFF003731),
    tertiaryContainer = Color(0xFF00695C),
    onTertiaryContainer = Color(0xFFB2DFDB),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    errorContainer = Color(0xFF93000A),
    onErrorContainer = Color(0xFFFFDAD6),
    background = Color(0xFF1C1B1F),
    onSurface = Color(0xFFE6E1E5),
    onSurfaceVariant = Color(0xFFCAC4D0),
    outline = Color(0xFF938F99),
    outlineVariant = Color(0xFF49454F),
)

@Composable
fun MemlyTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = WearColorScheme,
        content = content,
    )
}
