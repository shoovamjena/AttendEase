package com.example.attendease.color

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ColorComponent(
    modifier: Modifier = Modifier, color: ColorModel
) {
    Card(
        modifier = modifier
            .wrapContentSize()
            .clip(CircleShape),
        shape = CircleShape,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .border(2.dp, Color.White, CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(color.startColor, color.endColor)
                    ), shape = CircleShape
                )
        )
    }
}