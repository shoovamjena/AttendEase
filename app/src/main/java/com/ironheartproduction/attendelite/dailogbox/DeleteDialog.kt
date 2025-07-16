package com.ironheartproduction.attendelite.dailogbox

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ironheartproduction.attendelite.R
import com.ironheartproduction.attendelite.ui.theme.nothingFontFamily
import com.ironheartproduction.attendelite.ui.theme.roundFontFamily

@Composable
fun DeleteDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    containerColor: Color,
    text: String,
    toast: String,
    isAndroid12OrAbove: Boolean
){
    val context = LocalContext.current
    var playAnimation by remember { mutableStateOf(false) }
    var isAnimationPlayed by remember { mutableStateOf(false) }

    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(R.raw.delete))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        isPlaying = playAnimation,
        iterations = 1,
        speed = 1f,
    )
    LaunchedEffect(progress) {
        if (playAnimation && progress == 1f && !isAnimationPlayed) {
            isAnimationPlayed = true
            onConfirm()
            onDismiss()
            Toast.makeText(context,toast,Toast.LENGTH_LONG).show()
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(
                    color = containerColor.copy(alpha = 1f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (playAnimation) {
                Text(
                    text = "DELETE CONFIRMED",
                    fontFamily = nothingFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 24.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                LottieAnimation(
                    composition = composition,
                    progress = { progress },
                    modifier = Modifier
                        .height(90.dp)
                        .fillMaxWidth()
                )
                }else {
                    Text(
                        text = "CONFIRM DELETE",
                        fontFamily = nothingFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 26.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = text,
                        fontFamily = roundFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary
                    )

                    androidx.compose.foundation.Image(
                        painter = androidx.compose.ui.res.painterResource(id = R.drawable.deleteicon),
                        contentDescription = "Delete Icon",
                        modifier = Modifier
                            .height(90.dp)
                            .fillMaxWidth()
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(
                            onClick = onDismiss
                        ) {
                            Text(text = "CANCEL", fontFamily = nothingFontFamily)
                        }
                        Button(
                            onClick = { playAnimation = true },
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                            modifier = Modifier.shadow(if(isAndroid12OrAbove)5.dp else 0.dp, shape = RoundedCornerShape(50))
                        ) {
                            Text(text = "CONFIRM", color = MaterialTheme.colorScheme.onPrimary, fontFamily = nothingFontFamily)
                        }
                    }
                }

            }
        }
    }
}