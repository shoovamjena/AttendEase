package com.ironheartproduction.attendelite.screen

import android.app.Activity
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import com.ironheartproduction.attendelite.R
import com.ironheartproduction.attendelite.UserPreferences
import com.ironheartproduction.attendelite.dailogbox.NoInternetDialog
import com.ironheartproduction.attendelite.dailogbox.PaymentFailureDialog
import com.ironheartproduction.attendelite.dailogbox.PaymentSuccessDialog
import com.ironheartproduction.attendelite.dailogbox.SupportDialog
import com.ironheartproduction.attendelite.payment.PaymentState
import com.ironheartproduction.attendelite.ui.theme.ThemePreference
import com.ironheartproduction.attendelite.ui.theme.nothingFontFamily
import com.ironheartproduction.attendelite.ui.theme.roundFontFamily
import com.ironheartproduction.attendelite.uicomponent.SupportItem
import com.ironheartproduction.attendelite.uicomponent.TypeWriterWithCursor
import com.ironheartproduction.attendelite.uicomponent.bottomnavbar.BottomNavNoAnimation
import com.ironheartproduction.attendelite.uicomponent.bottomnavbar.Screen
import com.ironheartproduction.attendelite.viewmodel.PaymentViewModel
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
    navController: NavController = rememberNavController(),
    viewModel: PaymentViewModel,
    userPreference: UserPreferences
) {

    val themePref by userPreference.themePreferenceFlow.collectAsState(initial = ThemePreference.LIGHT)
    val isDark = when (themePref) {
        ThemePreference.DARK -> true
        ThemePreference.LIGHT -> false
        ThemePreference.SYSTEM_DEFAULT -> isSystemInDarkTheme()
    }

    val context = LocalContext.current
    var donateDialog by remember { mutableStateOf(false) }
    var showNoInternetDialog by remember { mutableStateOf(false) }
    var paymentFailureDialog by remember { mutableStateOf(false) }
    var paymentSuccessDialog by remember { mutableStateOf(false) }
    var selectedLottie by remember { mutableStateOf<LottieCompositionSpec?>(null) }
    var selectedTitle by remember { mutableStateOf("") }
    var selectedAmount by remember { mutableIntStateOf(0) }

    var paymentId by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val paymentState by viewModel.paymentState.collectAsState()

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
        MaterialTheme.colorScheme.primaryContainer
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
            Toast.makeText(context,"Live Donations will be Activated soon", Toast.LENGTH_LONG).show()
            viewModel.startPayment(context as Activity,selectedAmount,selectedTitle,color=contentColor)
            viewModel.userName = userName
        } else {
            showNoInternetDialog = true
        }
    }

    if(paymentSuccessDialog){
        PaymentSuccessDialog(
            onDismiss = { paymentSuccessDialog = false
                        viewModel.resetPaymentState()},
            cardColor = contentColor,
            paymentId = paymentId,
            isAndroid12OrAbove = isAndroid12OrAbove
        )
    }

    if(paymentFailureDialog){
        PaymentFailureDialog(
            onDismiss = { paymentFailureDialog = false
                        viewModel.resetPaymentState()},
            cardColor = contentColor,
            errorMessage = errorMessage,
            isAndroid12OrAbove = isAndroid12OrAbove
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
                onConfirm = { processPayment() },
                isAndroid12OrAbove = isAndroid12OrAbove
            )
        }
    }

    if(showNoInternetDialog){
        NoInternetDialog(
            onDismiss = {showNoInternetDialog = false},
            cardColor = contentColor,
            isAndroid12OrAbove = isAndroid12OrAbove
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
                            .shadow(26.dp, shape = RoundedCornerShape(50))
                            .clip(RoundedCornerShape(percent = 50))
                            .background(contentColor.copy(alpha = 0.1f)),
                    ) {
                        BottomNavNoAnimation(
                            screens = screen,
                            contentColor,
                            if(isLava && isDark) MaterialTheme.colorScheme.onPrimaryContainer.copy(0.5f)
                            else{
                                if(isDark) MaterialTheme.colorScheme.primary.copy(0.5f)
                                else backgroundColor.copy(alpha = 0.5f)},
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
                            text = "ATTENDELITE",
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
                            .heightIn(max = 600.dp),
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
                            },
                            isDark = isDark
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
                            },
                            isDark = isDark
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
                            },
                            isDark = isDark
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
                            },
                            isDark = isDark
                        )
                        SupportItem(
                            contentColor = contentColor,
                            lottieCompositionSpec = LottieCompositionSpec.RawRes(R.raw.gift),
                            title = "GIFT ME",
                            amount = 999,
                            onSelect = {
                                selectedLottie = LottieCompositionSpec.RawRes(R.raw.gift)
                                selectedAmount = 999
                                selectedTitle = "GIFT ME"
                                donateDialog = true
                            },
                            isDark = isDark
                        )
                        Spacer(modifier = Modifier.height(50.dp))
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
                            color = if(isDark) MaterialTheme.colorScheme.primary else contentColor
                        )
                    }
                }
            }
        }
    }
}

