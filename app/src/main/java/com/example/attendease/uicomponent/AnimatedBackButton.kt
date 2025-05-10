package com.example.attendease.uicomponent

import android.annotation.SuppressLint
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.attendease.ui.theme.nothingFontFamily
import com.example.attendease.uicomponent.bottomnavbar.Screen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun AnimatedBackButton(navController: NavController, color: Color) {
    val scope = rememberCoroutineScope()
    var animate by remember { mutableStateOf(false) }


    val textAlpha by animateFloatAsState(
        targetValue = if (animate) 0f else 1f,
        animationSpec = tween(durationMillis = 700), label = ""
    )

    val arrowOffsetX by animateDpAsState(
        targetValue = if (animate) (-300).dp else 300.dp,
        animationSpec = tween(durationMillis = 800), label = ""
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(color) // You can customize
            .clickable {
                animate = true
                scope.launch {
                    delay(800) // wait for animation to complete
                    withContext(Dispatchers.Main){
                        navController.navigate(Screen.Home.route) {
                            popUpTo(0) // Clears backstack
                        }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "BACK",
            color = MaterialTheme.colorScheme.primary,
            fontFamily = nothingFontFamily,
            fontSize = 28.sp,
            modifier = Modifier.alpha(textAlpha)
        )

        Icon(
            imageVector = Icons.AutoMirrored.Filled.ArrowBack, // Material Icons ka Arrow
            contentDescription = "Back Arrow",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .offset(x = arrowOffsetX)
                .size(36.dp)
        )
    }
}
