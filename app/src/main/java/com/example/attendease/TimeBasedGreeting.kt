package com.example.attendease

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
    val contentColor = MaterialTheme.colorScheme.primary
    val greetingMessage = when{
        currentTime in 4..11 -> "Good Morning!!"
        currentTime in 12..15 -> "Good Afternoon!!"
        currentTime in 16..22 -> "Good Evening!!"
        else -> "Good Night!!"
    }

    Text(
        text = greetingMessage,
        color = contentColor,
        fontSize = 32.sp,
        fontFamily = nothingFontFamily,
        fontWeight = FontWeight.ExtraBold,
        modifier = Modifier.alpha(0.6f)
    )

}