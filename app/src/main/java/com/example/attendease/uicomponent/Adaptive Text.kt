package com.example.attendease.uicomponent

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp

@Composable
fun AdaptiveText(
    text: String,
    baseFontSize:Int ,
    decreaseStep: Int ,
    minimumSize: Int,
    modifier: Modifier,
    fontFamily: FontFamily
) {
    val length = text.length
    val reduceFactor = (length / 5)
    val calculatedFontSize = (baseFontSize - (reduceFactor * decreaseStep)).coerceAtLeast(minimumSize)

    Text(
        text = text,
        fontSize = calculatedFontSize.sp,
        modifier = modifier,
        fontFamily = fontFamily,
        textAlign = TextAlign.Center
    )
}
