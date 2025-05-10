package com.example.attendease.uicomponent

import android.os.Build
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendease.ui.theme.nothingFontFamily
import java.text.SimpleDateFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.util.Date
import java.util.Locale

@Composable
fun Weekly(
    modifier: Modifier = Modifier,
    onDateSelected: (String) -> Unit,
    color: Color
){

    val today = remember { LocalDate.now() }
    fun getTodayDay(): String {
        val sdf = SimpleDateFormat("EEE", Locale.getDefault())
        return sdf.format(Date())
    }
    val currentDay = remember { getTodayDay() }



    val startOfWeek = today.with(DayOfWeek.MONDAY)

    val weekDays = remember {
        (0..6).map {
            val date = startOfWeek.plusDays(it.toLong())
            DayItem(
                dayOfWeek = date.dayOfWeek.name.take(3), // e.g. "MON"
                date = date
            )
        }
    }

    var selectedDate by remember { mutableStateOf(today) }
    var selectedDay by remember { mutableStateOf(currentDay) }
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by remember { mutableStateOf(false) }
    val isLava = Build.BRAND.equals("lava", ignoreCase = true)



    LazyRow(
        modifier = modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        items(weekDays) { day ->
            val isSelected = day.date == selectedDate
            val isToday = day.date == today

            val scale by animateFloatAsState(
                targetValue = if (isPressed) 0.5f else 1f,
                animationSpec = tween(durationMillis = 150),
                label = "scaleAnim"
            )

            val backgroundColor by animateColorAsState(
                targetValue = when {
                    isSelected -> if(isLava){MaterialTheme.colorScheme.onPrimary}else{MaterialTheme.colorScheme.primaryContainer}
                    else -> if(isLava){
                        MaterialTheme.colorScheme.onPrimary.copy(0.1f)
                    }else{MaterialTheme.colorScheme.primaryContainer.copy(0.1f)}
                },
                animationSpec = tween(durationMillis = 700, easing  = FastOutSlowInEasing),
                label = "bgColorAnim"
            )

            val borderColor by animateColorAsState(
                targetValue = if (isToday && !isSelected) MaterialTheme.colorScheme.primary else if(isLava){MaterialTheme.colorScheme.onPrimary}else{MaterialTheme.colorScheme.primaryContainer},
                animationSpec = tween(durationMillis = 600),
                label = "borderColorAnim"
            )

            val textColor = if (isSelected) Color.White else color

            Column(
                modifier = Modifier
                    .padding(4.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    }
                    .heightIn(min = 50.dp)
                    .widthIn(min = 50.dp)
                    .clip(RoundedCornerShape(25))
                    .background(backgroundColor)
                    .border(
                        width = 3.dp,
                        color = borderColor,
                        shape = RoundedCornerShape(25)
                    )
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        selectedDay = day.dayOfWeek
                        selectedDate = day.date
                        onDateSelected(selectedDay)
                    }
                    .padding(vertical = 8.dp, horizontal = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = day.dayOfWeek,
                    fontSize = 18.sp,
                    color = textColor,
                    fontFamily = nothingFontFamily
                )
                Text(
                    text = day.date.dayOfMonth.toString(),
                    color = textColor,
                    fontFamily = nothingFontFamily
                )
            }
        }
    }
}

data class DayItem(
    val dayOfWeek: String,
    val date: LocalDate
)