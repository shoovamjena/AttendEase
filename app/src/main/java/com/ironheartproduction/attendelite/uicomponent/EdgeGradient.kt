package com.ironheartproduction.attendelite.uicomponent

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun HorizontalEdgeGradient(
    modifier: Modifier = Modifier,
    width: Dp = 25.dp,
    color1: Color,
    color2: Color
) {
    Box(
        modifier = modifier
            .fillMaxHeight()
            .width(width)
            .drawWithCache {
                onDrawBehind {
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(color1, color2),
                            start = Offset(0f, 0f),
                            end = Offset(size.width, 0f)
                        )
                    )
                }
            }
    )
}

@Composable
fun VerticalEdgeGradient(
    modifier: Modifier = Modifier,
    height: Dp = 35.dp,
    color1: Color,
    color2: Color
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .drawWithCache {
                onDrawBehind {
                    drawRect(
                        brush = Brush.linearGradient(
                            colors = listOf(color1, color2),
                            start = Offset(0f, size.height),
                            end = Offset(0f, 0f)
                        )
                    )
                }
            }
    )
}
