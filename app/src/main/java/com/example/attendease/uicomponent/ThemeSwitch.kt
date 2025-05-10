package com.example.attendease.uicomponent

import android.annotation.SuppressLint
import android.os.Build
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.attendease.R
import com.example.attendease.UserPreferences
import com.example.attendease.ui.theme.ThemePreference
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("UseOfNonLambdaOffsetOverload")
@Composable
fun ThemeSwitch(
    staticColor: Color,
    selectedColor: Color,
    selectedBgColor: Color,
    staticBgColor: Color,
    userPreferences: UserPreferences
) {
    val isAndroid12OrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val scope = rememberCoroutineScope()
    val systemIsDark = false
    val themePref by userPreferences.themePreferenceFlow.collectAsState(initial = ThemePreference.SYSTEM_DEFAULT)

    var isSwitchOn by remember { mutableStateOf(systemIsDark)}

    LaunchedEffect(themePref) {
        delay(500)
        isSwitchOn = themePref == ThemePreference.DARK
    }

    val bgColor by animateColorAsState(
        targetValue = if (isSwitchOn) selectedBgColor else staticBgColor,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "Animated Background"
    )

    val thumbColor by animateColorAsState(
        targetValue = if (isSwitchOn) selectedColor else staticColor,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "Animated Thumb"
    )

    val rotation by animateFloatAsState(
        targetValue = if (isSwitchOn) 360f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "Rotation"
    )

    Box(
        modifier = Modifier
            .widthIn(max = 100.dp)
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .requiredHeightIn(40.dp)
                .shadow(
                    if (isAndroid12OrAbove) {
                        25.dp
                    } else 0.dp
                )
                .shadow(0.dp,shape = CircleShape)
                .clip(RoundedCornerShape(25.dp))
                .background(bgColor)
                .clickable {
                    isSwitchOn = !isSwitchOn
                    scope.launch {
                        userPreferences.saveThemePreference(
                            if (isSwitchOn) ThemePreference.DARK else ThemePreference.LIGHT
                        )
                    }
                },
            contentAlignment = Alignment.CenterStart
        ) {
            val maxWidth = this.maxWidth
            val thumbWidth = 35.dp
            val horizontalPadding = 3.dp
            val maxOffset = maxWidth - thumbWidth - horizontalPadding-1.dp

            val offset by animateDpAsState(
                targetValue = if (isSwitchOn) maxOffset else horizontalPadding,
                animationSpec = tween(500, easing = FastOutSlowInEasing),
                label = "Offset"
            )

            Box(
                modifier = Modifier
                    .height(35.dp)
                    .offset(x = offset)
                    .clip(CircleShape)
                    .background(thumbColor)
            ) {
                Crossfade(
                    targetState = isSwitchOn,
                    animationSpec = tween(300),
                    label = "Icon Cross fade"
                ) { targetState ->
                    Box(
                        modifier = Modifier
                            .padding(6.dp)
                            .size(24.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(
                                id = if (targetState) R.drawable.darkmode else R.drawable.light_mode
                            ),
                            contentDescription = "Notification Toggle",
                            modifier = Modifier
                                .rotate(rotation),
                            tint = if (isSwitchOn) Color.White else Color.Black
                        )
                    }
                }
            }
        }
    }
}
