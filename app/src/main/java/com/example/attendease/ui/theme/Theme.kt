package com.example.attendease.ui.theme

import android.os.Build
import android.util.Log

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

enum class ThemePreference {
    LIGHT, DARK, SYSTEM_DEFAULT
}

@Composable
fun AttendEaseTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
    selectedColor: Int? = null,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val supportsDynamic = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val dynamicScheme = try {
        if (darkTheme) dynamicDarkColorScheme(context)
        else dynamicLightColorScheme(context)
    } catch (e: Exception) {
        null
    }
    Log.d("ThemeDebug", "Primary Color: ${dynamicScheme?.primary}")


    // Function to detect if dynamic primary color is “ugly” or invisible
    fun isWeirdColor(color: Color): Boolean {
        return color.red + color.green + color.blue > 2.5 // too white-ish
    }

    val colorScheme = when {
        dynamicColor && supportsDynamic && dynamicScheme != null && !isWeirdColor(dynamicScheme.primary) -> {
            dynamicScheme
        }

        selectedColor != null -> {
            lightColorScheme(
                primary = Color(selectedColor),
                secondary = Color(selectedColor),
                onPrimary = Color.White,
                onSecondary = Color.White,
                surface = Color.White,
                onSurface = Color.Black
            )
        }

        else -> {
            lightColorScheme(
                primary = Color(0xFF6750A4), // your old purple
                secondary = Color(0xFF625B71),
                tertiary = Color(0xFF7D5260),
                onPrimary = Color.White,
                surface = Color.White,
                onSurface = Color.Black
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

