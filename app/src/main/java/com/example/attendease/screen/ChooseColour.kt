package com.example.attendease.screen

import android.content.Intent
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.example.attendease.MainActivity
import com.example.attendease.UserPreferences
import com.example.attendease.color.ColorComponent
import com.example.attendease.color.ColorList
import com.example.attendease.color.Data
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.math.hypot

@Composable
fun ChooseColorScreen(
) {
    val colors = remember { Data.colors }
    var previousBackgroundColor by remember { mutableStateOf(colors.first()) }
    var currentBackgroundColor by remember { mutableStateOf(colors.first()) }
    val (width, height) = with(LocalConfiguration.current) {
        with(LocalDensity.current) { screenWidthDp.dp.toPx() to screenHeightDp.dp.toPx() }
    }
    val maxRadiusPx = hypot(width, height)
    var radius by remember { mutableFloatStateOf(0f) }
    var clickedOffset: Offset? by remember { mutableStateOf(null) }
    val animatedRadius = remember { Animatable(0f) }
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val coroutineScope = rememberCoroutineScope()
    var selectedColor by remember { mutableStateOf(Color.Transparent) }
    LaunchedEffect(key1 = true) {
        snapshotFlow { currentBackgroundColor }.collectLatest {
            animatedRadius.animateTo(
                maxRadiusPx,
                animationSpec = tween(500, easing = LinearEasing)
            ) {
                radius = value

            }
            animatedRadius.animateTo(0f, animationSpec = tween(durationMillis = 50)) {
                previousBackgroundColor = currentBackgroundColor
            }
        }
    }

    Box(modifier = Modifier
        .fillMaxSize()
        .drawWithCache {
            onDrawBehind {
                drawRect(
                    Brush.linearGradient(
                        listOf(
                            previousBackgroundColor.startColor, previousBackgroundColor.endColor
                        )
                    )
                )
                drawCircle(
                    brush = Brush.linearGradient(
                        listOf(
                            currentBackgroundColor.startColor, currentBackgroundColor.endColor
                        )
                    ),
                    radius = radius,
                    center = clickedOffset ?: Offset(size.width / 2, size.height / 2),
                )
            }
        }) {
        ColorList(visibleItems = 3,
            modifier = Modifier.fillMaxWidth(),
            onItemClick = { selectedIndex, offset ->
                clickedOffset = offset
                currentBackgroundColor = colors[selectedIndex]
                selectedColor = colors[selectedIndex].startColor
            }) {
            colors.forEachIndexed { _, color ->
                ColorComponent(color = color)
            }
        }
        Button(onClick = {
            coroutineScope.launch {
                userPreferences.saveThemeColor(selectedColor.toArgb())
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }
        },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text("CONFIRM")
        }
    }
}

