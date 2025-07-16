package com.ironheartproduction.attendelite.uicomponent

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import com.ironheartproduction.attendelite.ui.theme.nothingFontFamily
import kotlinx.coroutines.delay

@Composable
fun TypeWriterWithCursor(
    text: String,
    modifier: Modifier = Modifier,
    typingSpeed: Long = 100L,
    pauseDuration: Long = 2500L,
    fontSize: Int,
    color: Color// pause before restart
) {
    var visibleText by remember { mutableStateOf("") }
    var showCursor by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        while (true) {
            showCursor = true
            delay(500L)
            showCursor = false
            delay(500L)
        }
    }

    LaunchedEffect(text) {
        while (true) {
            for (i in 1..text.length) {
                visibleText = text.substring(0, i)
                delay(typingSpeed)
            }
            delay(pauseDuration)
            for (i in text.length downTo 1) {
                visibleText = text.substring(0, i-1)
                delay(typingSpeed)
            }
            delay(pauseDuration-1000)
            visibleText = ""
        }
    }


    Text(
        text = visibleText + if (showCursor) "|" else "",
        modifier = modifier,
        fontFamily = nothingFontFamily,
        fontSize = fontSize.sp,
        color = color
    )
}
