package com.ironheartproduction.attendelite.uicomponent

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ironheartproduction.attendelite.ui.theme.nothingFontFamily
import com.ironheartproduction.attendelite.ui.theme.roundFontFamily

@Composable
fun SupportItem(
    contentColor: Color,
    lottieCompositionSpec: LottieCompositionSpec,
    title: String,
    amount: Int,
    onSelect: () -> Unit,
    isDark: Boolean
){
    val composition by rememberLottieComposition(
        spec = lottieCompositionSpec
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        restartOnPlay = false
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(max = 80.dp)
            .padding(top = 20.dp)
            .shadow(15.dp, shape = RoundedCornerShape(50))
            .clip(
                RoundedCornerShape(50)
            )
            .background(contentColor)
            .clickable { onSelect() },
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = title,
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
                fontFamily = nothingFontFamily,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.alpha(0.6f)
            )
            Spacer(modifier = Modifier.weight(0.1f))
            Text(
                text = "(₹$amount)",
                color = if(isDark) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.secondary,
                fontSize = 14.sp,
                fontFamily = roundFontFamily,
                fontWeight = FontWeight.ExtraBold,
                modifier = Modifier.alpha(0.6f)
            )
            Spacer(modifier = Modifier.weight(1f))
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier
                    .size(70.dp)
                    .padding(vertical = 7.dp),
            )
        }
    }
}