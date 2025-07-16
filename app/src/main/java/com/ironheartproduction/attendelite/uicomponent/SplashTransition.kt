package com.ironheartproduction.attendelite.uicomponent

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironheartproduction.attendelite.ui.theme.nothingFontFamily
import kotlinx.coroutines.delay

@Composable
fun SplashTransition(onAnimationEnd: () -> Unit,backgroundColor:Color,contentColor:Color){
    var isReady by remember { mutableStateOf(false) }
    var transitionColor by remember { mutableStateOf(false) }

    val circleScale by animateFloatAsState(
        targetValue = if (transitionColor) 100f else 0f,
        animationSpec = tween(durationMillis = 3000, easing = FastOutSlowInEasing),
        label = "circleScale"
    )
    LaunchedEffect(Unit) {
        delay(1500)
        transitionColor = true
        delay(500)
        isReady = true
        onAnimationEnd()
    }

    if (!isReady) {
        var dot by remember { mutableIntStateOf(0) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "LOADING"+".".repeat(dot), fontSize = 42.sp, fontFamily = nothingFontFamily, fontWeight = FontWeight.Bold, color = contentColor)
            if (transitionColor) {
                Box(
                    modifier = Modifier
                        .height(300.dp)
                        .width(300.dp)
                        .graphicsLayer {
                            scaleX = circleScale
                            scaleY = circleScale
                            transformOrigin = TransformOrigin.Center
                        }
                        .clip(RoundedCornerShape(100))
                        .background(contentColor)
                )
            }}
        LaunchedEffect(Unit){
            while(!isReady){
                delay(100)
                dot =(dot+1)%5
            }
        }
    }
}