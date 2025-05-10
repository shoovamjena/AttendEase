package com.example.attendease.uicomponent.bottomnavbar

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter

@Composable
fun FlipIcon(
    modifier: Modifier = Modifier,
    isActive: Boolean,
    activeIcon: @Composable () -> ImageVector,
    inactiveIcon: @Composable () -> ImageVector,
    contentDescription: String,
) {

    val animationRotation by animateFloatAsState(
        targetValue = if (isActive) 360f else 0f,
        animationSpec = spring(
            stiffness = Spring.StiffnessLow,
            dampingRatio = Spring.DampingRatioMediumBouncy
        ), label = ""
    )
    Box(
        modifier = modifier
            .graphicsLayer { rotationY = animationRotation },
        contentAlignment = Alignment.Center,
    ) {
        val icon = if (animationRotation > 270f) activeIcon() else inactiveIcon()
        Icon(
            painter = rememberVectorPainter(image = icon),
            contentDescription = contentDescription,
            tint = MaterialTheme.colorScheme.primary
        )
    }
}