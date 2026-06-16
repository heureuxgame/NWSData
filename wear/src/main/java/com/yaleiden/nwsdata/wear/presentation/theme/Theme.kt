package com.yaleiden.nwsdata.wear.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme

@Composable
fun EsFISHWXTheme(
    content: @Composable () -> Unit
) {
    // Manually build the ColorScheme to guarantee compilation regardless of library updates
    val whiteTextColorScheme = ColorScheme(
        primary = Color(0xFF6366F1),           // Your Indigo/Blue accent
        onPrimary = Color(0xFFFFFFFF),         // Text inside primary buttons
        onSurface = Color(0xFFFFFFFF),         // Primary text (e.g., Lake Level)
        onSurfaceVariant = Color(0xFFFFFFFF),  // Secondary text (e.g., Site Name)

        // Baseline fallback requirements for standard Wear layouts
        background = Color(0xFF000000),        // Pitch black for battery saving on OLED screens
        error = Color(0xFFFFB4AB),
        onError = Color(0xFF690005)
    )

    MaterialTheme(
        colorScheme = whiteTextColorScheme,
        content = content
    )
}