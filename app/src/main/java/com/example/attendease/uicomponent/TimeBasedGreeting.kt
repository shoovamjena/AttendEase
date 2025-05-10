package com.example.attendease.uicomponent

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.attendease.ui.theme.nothingFontFamily
import java.util.Calendar

@Composable
fun TimeBasedGreeting (){
    val currentTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
    val greetingMessage = when (currentTime) {
        in 4..11 -> "Good Morning!!"
        in 12..15 -> "Good Afternoon!!"
        else -> "Good Evening!!"
    }

    Text(
        text = greetingMessage,
        color = MaterialTheme.colorScheme.primary,
        fontSize = 28.sp,
        fontFamily = nothingFontFamily,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.alpha(0.6f)
    )

}