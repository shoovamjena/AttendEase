package com.example.attendease.dailogbox

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.attendease.R
import com.example.attendease.ui.theme.nothingFontFamily
import com.example.attendease.ui.theme.roundFontFamily

@Composable
fun NoInternetDialog(
    onDismiss: () -> Unit,
    cardColor: Color,
    isAndroid12OrAbove: Boolean
){
    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .shadow(if(isAndroid12OrAbove)5.dp else 0.dp, shape = RoundedCornerShape(16.dp))
                .clip(RoundedCornerShape(16.dp))
                .background(cardColor),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "NO INTERNET CONNECTION",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    fontFamily = nothingFontFamily
                )

                Spacer(modifier = Modifier.height(16.dp))

                // No Internet Lottie Animation
                val noInternetComposition by rememberLottieComposition(
                    spec = LottieCompositionSpec.RawRes(R.raw.nointernet)
                )
                val noInternetAnimation = rememberLottieAnimatable()

                LaunchedEffect(noInternetComposition) {
                    if (noInternetComposition != null) {
                        noInternetAnimation.animate(
                            composition = noInternetComposition,
                            iterations = LottieConstants.IterateForever,
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .size(150.dp),
                    contentAlignment = Alignment.Center
                ) {
                    LottieAnimation(
                        composition = noInternetComposition,
                        progress = { noInternetAnimation.progress },
                        modifier = Modifier.size(100.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Please check your internet connection and try again.",
                    textAlign = TextAlign.Center,
                    fontFamily = roundFontFamily,
                    fontSize = 10.sp
                )

                Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick= onDismiss,
                        modifier = Modifier.align(Alignment.CenterHorizontally)
                            .shadow(if(isAndroid12OrAbove)5.dp else 0.dp, shape = RoundedCornerShape(50)),
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                    ) {
                        Text("TRY AGAIN",
                            fontFamily = nothingFontFamily)
                    }
            }
        }
    }
}