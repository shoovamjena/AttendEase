package com.example.attendease.screen

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.attendease.R
import com.example.attendease.dailogbox.NoInternetDialog
import com.example.attendease.dailogbox.PaymentFailureDialog
import com.example.attendease.dailogbox.PaymentSuccessDialog
import com.example.attendease.dailogbox.SupportDialog
import com.example.attendease.model.PaymentViewModel
import com.example.attendease.payment.PaymentState
import com.example.attendease.ui.theme.nothingFontFamily
import com.example.attendease.ui.theme.roundFontFamily
import com.example.attendease.uicomponent.SupportItem
import com.example.attendease.uicomponent.TypeWriterWithCursor
import com.example.attendease.uicomponent.bottomnavbar.BottomNavNoAnimation
import com.example.attendease.uicomponent.bottomnavbar.Screen
import kotlinx.coroutines.delay

fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    val network = connectivityManager.activeNetwork ?: return false
    val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false

    return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
            capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
}

@Composable
fun DonateScreen(
    userName: String,
    selectedColor: Int?,
    navController: NavController = rememberNavController(),
    viewModel: PaymentViewModel
) {
    val context = LocalContext.current
    var donateDialog by remember { mutableStateOf(false) }
    var showNoInternetDialog by remember { mutableStateOf(false) }
    var paymentFailureDialog by remember { mutableStateOf(false) }
    var paymentSuccessDialog by remember { mutableStateOf(false) }
    var showCustomAmountDialog by remember { mutableStateOf(false) }
    var selectedLottie by remember { mutableStateOf<LottieCompositionSpec?>(null) }
    var selectedTitle by remember { mutableStateOf("") }
    var selectedAmount by remember { mutableIntStateOf(0) }

    var paymentId by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val paymentState by viewModel.paymentState.collectAsState()

    val selectColor = selectedColor?.let { Color(it) } ?: MaterialTheme.colorScheme.primary
    val isLava = Build.BRAND.equals("lava", ignoreCase = true)
    val isAndroid12OrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    val backgroundColor = if(isLava){
        MaterialTheme.colorScheme.onPrimaryContainer
    }else {

        MaterialTheme.colorScheme.onPrimary
    }
    val contentColor = if (isLava){
        MaterialTheme.colorScheme.onPrimary
    }else {
        if (isAndroid12OrAbove) {
            MaterialTheme.colorScheme.primaryContainer
        } else {
            selectColor
        }
    }

    val screen = listOf(
        Screen.Home,
        Screen.Timetable,
        Screen.Donate,
        Screen.Settings
    )

    val composition1 by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.heart)
    )

    val progress1 by animateLottieCompositionAsState(
        composition = composition1,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        restartOnPlay = false
    )
    var isReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isReady = true
    }

    LaunchedEffect(paymentState) {
        when (val state = paymentState) {
            is PaymentState.Success -> {
                paymentSuccessDialog = true
                paymentId = state.paymentId
            }
            is PaymentState.Error -> {
                paymentFailureDialog = true
                errorMessage = state.message

            }
            else -> {}
        }
    }


    fun processPayment() {
        if (isInternetAvailable(context)) {
            viewModel.startPayment(context as Activity,selectedAmount,selectedTitle,color=contentColor)
            viewModel.userName = userName
//            Toast.makeText(context,"Donations will be implemented soon",Toast.LENGTH_LONG).show()
        } else {
            showNoInternetDialog = true
        }
    }

    if(paymentSuccessDialog){
        PaymentSuccessDialog(
            onDismiss = { paymentSuccessDialog = false
                        viewModel.resetPaymentState()},
            cardColor = contentColor,
            paymentId = paymentId
        )
    }

    if(paymentFailureDialog){
        PaymentFailureDialog(
            onDismiss = { paymentFailureDialog = false
                        viewModel.resetPaymentState()},
            cardColor = contentColor,
            errorMessage = errorMessage
        )
    }

    if (donateDialog){
        selectedLottie?.let {
            SupportDialog(
                onDismiss = {donateDialog = false},
                title = selectedTitle,
                amount = selectedAmount,
                lottieCompositionSpec = it,
                containerColor = contentColor,
                onConfirm = { processPayment() }
            )
        }
    }

    if(showNoInternetDialog){
        NoInternetDialog(
            onDismiss = {showNoInternetDialog = false},
            cardColor = contentColor
        )
    }

    if (!isReady) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent), contentAlignment = Alignment.Center) {
            Text(text = "LOADING...", fontSize = 42.sp, fontFamily = nothingFontFamily, fontWeight = FontWeight.Bold, color = contentColor)        }
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && donateDialog) {
                        renderEffect =
                            BlurEffect(radiusX = 10.dp.toPx(), radiusY = 10.dp.toPx())
                    }
                },
            bottomBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .heightIn(min = 30.dp)
                        .windowInsetsPadding(WindowInsets.navigationBars)

                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                            .shadow(26.dp)
                            .clip(RoundedCornerShape(percent = 50))
                            .background(contentColor.copy(alpha = 0.1f)),
                    ) {
                        BottomNavNoAnimation(
                            screens = screen,
                            contentColor,
                            backgroundColor.copy(alpha = 0.5f),
                            navController,
                            2
                        )
                    }
                }
            }
        ) { _ ->
            Box(modifier = Modifier
                .fillMaxSize()
                ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 30.dp)
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .clip(RoundedCornerShape(50))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ATTENDEASE",
                            color = contentColor,
                            fontSize = 38.sp,
                            textAlign = TextAlign.Center,
                            fontFamily = roundFontFamily,
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 100.dp, start = 10.dp, end = 10.dp)
                ) {

                    Column(
                        modifier = Modifier
                            .padding(top = 10.dp, bottom = 80.dp)
                            .heightIn(max = 600.dp)
                            .clickable { showCustomAmountDialog = true },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Text(
                            text = "SUPPORT A DEVELOPER",
                            color = MaterialTheme.colorScheme.primary,
                            fontSize = 28.sp,
                            fontFamily = nothingFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            modifier = Modifier.alpha(0.6f)
                        )
                        SupportItem(
                            contentColor = contentColor,
                            lottieCompositionSpec = LottieCompositionSpec.RawRes(R.raw.tea),
                            title = "BUY ME A TEA",
                            amount = 49,
                            onSelect = {
                                selectedLottie = LottieCompositionSpec.RawRes(R.raw.tea)
                                selectedAmount = 49
                                selectedTitle = " BUY ME A TEA"
                                donateDialog = true
                            }
                        )
                        SupportItem(
                            contentColor = contentColor,
                            lottieCompositionSpec = LottieCompositionSpec.RawRes(R.raw.coffee),
                            title = "BUY ME A COFFEE",
                            amount = 99,
                            onSelect = {
                                selectedLottie = LottieCompositionSpec.RawRes(R.raw.coffee)
                                selectedAmount = 99
                                selectedTitle = " BUY ME A COFFEE"
                                donateDialog = true
                            }
                        )
                        SupportItem(
                            contentColor = contentColor,
                            lottieCompositionSpec = LottieCompositionSpec.RawRes(R.raw.netflix),
                            title = "BUY ME A NETFLIX SUB",
                            amount = 199,
                            onSelect = {
                                selectedLottie = LottieCompositionSpec.RawRes(R.raw.netflix)
                                selectedAmount = 199
                                selectedTitle = " BUY ME A NETFLIX SUB"
                                donateDialog = true
                            }
                        )
                        SupportItem(
                            contentColor = contentColor,
                            lottieCompositionSpec = LottieCompositionSpec.RawRes(R.raw.travel),
                            title = "BUY ME A TICKET",
                            amount = 499,
                            onSelect = {
                                selectedLottie = LottieCompositionSpec.RawRes(R.raw.travel)
                                selectedAmount = 499
                                selectedTitle = " BUY ME A TICKET"
                                donateDialog = true
                            }
                        )
                        Spacer(modifier = Modifier.height(100.dp))
                        Row {
                            Text(
                                "Developed with ",
                            )
                            LottieAnimation(
                                composition = composition1,
                                progress = { progress1 },
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        TypeWriterWithCursor(
                            "IRONHEART PRODUCTION",
                            fontSize = 26,
                            color = contentColor
                        )
                    }
                }
            }
        }
    }
}

