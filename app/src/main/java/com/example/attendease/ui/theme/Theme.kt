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
    fun isWeirdColor(color: Color): Boolean {
        return color.red + color.green + color.blue > 2.5 // too white-ish
    }

    val colorScheme = when {
        dynamicColor && supportsDynamic && dynamicScheme != null && !isWeirdColor(dynamicScheme.primary) -> {
            dynamicScheme
        }

        !darkTheme -> {
            lightColorScheme(
                tertiaryContainer = Color(0xffbfe9f9),
                tertiary = Color(0xFFb2dcec),
                primary = Color(0xFF00492c),
                primaryContainer = Color(0xFFA4E1BF),
                secondary = Color(0xFF2F4337),
                secondaryContainer = Color(0xFFA4E1BF),
                onPrimary = Color.White,
                onSecondary = Color.White,
                surface = Color.White,
                onSurface = Color.Black
            )
        }
        else -> {
            darkColorScheme(
                tertiaryContainer = Color(0xff234c59),
                tertiary= Color(0xFFb2dcec),
                primary =Color(0xFF88BFA0),
                primaryContainer = Color(0xFF2F4337),
                secondaryContainer = Color(0xFF88BFA0),
                onPrimary = Color.Black,
                onSecondary = Color.Black,
                surface = Color(0xFF121212),
                onSurface = Color.White
            )
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}

