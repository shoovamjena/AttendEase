package com.example.attendease.uicomponent

import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun AnimatedAttendanceProgressBar(
    attendancePercentage: Float,
    targetPercentage: Float =75f,
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = (attendancePercentage / 100f).coerceIn(0f, 1f),
        animationSpec = tween(durationMillis = 100, easing = FastOutLinearInEasing),
        label = "attendanceAnimation"
    )

    val barColor = if (animatedProgress < targetPercentage / 100f) Color(0xFFFF5252) else MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f)

    Box(
        modifier = modifier
            .height(20.dp)
            .width(160.dp)
            .border(2.dp, barColor, shape = RoundedCornerShape(12.dp))
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent, shape = RoundedCornerShape(12.dp))
                .padding( 5.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .fillMaxWidth(animatedProgress)
                    .background(barColor, shape = RoundedCornerShape(9.dp))
            )

        }
    }
}

