package com.pla.agen.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AntiGlareDark = darkColorScheme(
    primary = Color(0xFF8AB4F8),
    onPrimary = Color(0xFF0E0F12),
    secondary = Color(0xFFB7C4CF),
    background = Color(0xFF0B0C0F),
    onBackground = Color(0xFFE1E3E6),
    surface = Color(0xFF121318),
    onSurface = Color(0xFFDDDFE3),
    surfaceVariant = Color(0xFF1B1D22),
    onSurfaceVariant = Color(0xFFC4C7CC),
    outline = Color(0xFF3A3D44),
    error = Color(0xFFF2B8B5)
)

@Composable
fun PlaAgenTheme(content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = AntiGlareDark, content = content)
}
