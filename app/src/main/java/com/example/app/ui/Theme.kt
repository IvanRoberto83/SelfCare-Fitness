package com.example.app.ui

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// primary  = status "sudah olahraga"
// tertiary = status "dilewati"
private val LightColors = lightColorScheme(
    primary = Color(0xFF0B8F6A),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD3F4E6),
    onPrimaryContainer = Color(0xFF00382A),
    tertiary = Color(0xFFE5604F),
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFFE1DC),
    onTertiaryContainer = Color(0xFF5A1A12),
    background = Color(0xFFF4F9F6),
    onBackground = Color(0xFF16201C),
    surface = Color.White,
    onSurface = Color(0xFF16201C),
    surfaceVariant = Color(0xFFE6EEEA),
    onSurfaceVariant = Color(0xFF55645D),
    outline = Color(0xFFC5D1CB),
    outlineVariant = Color(0xFFDCE5E0),
    error = Color(0xFFB3261E),
    errorContainer = Color(0xFFFBE0DD),
    onErrorContainer = Color(0xFF5C1A15)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF58D6AA),
    onPrimary = Color(0xFF00382A),
    primaryContainer = Color(0xFF0F4D3B),
    onPrimaryContainer = Color(0xFFD3F4E6),
    tertiary = Color(0xFFFF8A7A),
    onTertiary = Color(0xFF4A150D),
    tertiaryContainer = Color(0xFF5A2A22),
    onTertiaryContainer = Color(0xFFFFE1DC),
    background = Color(0xFF0E1512),
    onBackground = Color(0xFFE2EAE6),
    surface = Color(0xFF151D19),
    onSurface = Color(0xFFE2EAE6),
    surfaceVariant = Color(0xFF25302B),
    onSurfaceVariant = Color(0xFFA7B6AE),
    outline = Color(0xFF3A4842),
    outlineVariant = Color(0xFF2B3731),
    error = Color(0xFFFFB4AB),
    errorContainer = Color(0xFF5C1A15),
    onErrorContainer = Color(0xFFFFDAD6)
)

private val AppShapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),
    large = RoundedCornerShape(28.dp)
)

@Composable
fun AppTheme(dark: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = if (dark) DarkColors else LightColors,
        shapes = AppShapes,
        content = content
    )
}
