package com.example.attendease.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.attendease.R

// Set of Material typography styles to start with
val Typography = Typography(
    bodyLarge = TextStyle(
        fontFamily = FontFamily.Default,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    )
)
 val coinyFontFamily = FontFamily(
     Font(R.font.coiny_regular, FontWeight.Normal),
     Font(R.font.coiny_regular, FontWeight.ExtraBold)
 )
val nothingFontFamily = FontFamily(
    Font(R.font.nothing_font, FontWeight.Normal),
    Font(R.font.nothing_font, FontWeight.ExtraBold)
)
val roundFontFamily = FontFamily(
    Font(R.font.rounds_black, FontWeight.ExtraBold)
)