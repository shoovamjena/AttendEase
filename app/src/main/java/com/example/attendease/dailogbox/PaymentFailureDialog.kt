package com.example.attendease.dailogbox

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.attendease.R
import com.example.attendease.ui.theme.nothingFontFamily
import com.example.attendease.ui.theme.roundFontFamily

@Composable
fun PaymentFailureDialog(
    onDismiss: () -> Unit,
    cardColor: Color,
    errorMessage: String,
    isAndroid12OrAbove: Boolean
){
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
                .heightIn(max = 500.dp)
                .padding(16.dp)
                .shadow(5.dp, shape = RoundedCornerShape(16.dp))
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
                    text = "PAYMENT FAILED",
                    fontWeight = FontWeight.Bold,
                    fontSize = 26.sp,
                    fontFamily = nothingFontFamily
                )


                // Payment Failure Lottie Animation
                val paymentFailureComposition by rememberLottieComposition(
                    spec = LottieCompositionSpec.RawRes(R.raw.paymentfailure)
                )
                val paymentFailureAnimation = rememberLottieAnimatable()

                LaunchedEffect(paymentFailureComposition) {
                    if (paymentFailureComposition != null) {
                        paymentFailureAnimation.animate(
                            composition = paymentFailureComposition,
                            iterations = LottieConstants.IterateForever,
                        )
                    }
                }


                    LottieAnimation(
                        composition = paymentFailureComposition,
                        progress = { paymentFailureAnimation.progress },
                        modifier = Modifier.size(250.dp)
                    )

                Text(
                    text = errorMessage,
                    textAlign = TextAlign.Center,
                    fontFamily = roundFontFamily,
                    fontSize = 10.sp
                )

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