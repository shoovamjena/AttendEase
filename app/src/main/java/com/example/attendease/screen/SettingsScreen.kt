package com.example.attendease.screen

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.attendease.R
import com.example.attendease.UserPreferences
import com.example.attendease.dailogbox.DeleteDialog
import com.example.attendease.viewmodel.SubjectViewModel
import com.example.attendease.viewmodel.TimetableViewModel
import com.example.attendease.ui.theme.ThemePreference
import com.example.attendease.ui.theme.nothingFontFamily
import com.example.attendease.ui.theme.roundFontFamily
import com.example.attendease.uicomponent.NotificationToggle
import com.example.attendease.uicomponent.ThemeSwitch
import com.example.attendease.uicomponent.TypeWriterWithCursor
import com.example.attendease.uicomponent.bottomnavbar.BottomNavNoAnimation
import com.example.attendease.uicomponent.bottomnavbar.Screen
import kotlinx.coroutines.delay

@Composable
fun SettingsScreen(
    navController: NavController = rememberNavController(),
    viewModel: SubjectViewModel,
    viewModel4: TimetableViewModel,
    userPreference: UserPreferences
) {
    val screen = listOf(
        Screen.Home,
        Screen.Timetable,
        Screen.Donate,
        Screen.Settings
    )

    val themePref by userPreference.themePreferenceFlow.collectAsState(initial = ThemePreference.LIGHT)
    val isDark = when (themePref) {
        ThemePreference.DARK -> true
        ThemePreference.LIGHT -> false
        ThemePreference.SYSTEM_DEFAULT -> isSystemInDarkTheme()
    }

    val context = LocalContext.current
    val activity = context as Activity
    val isLava = Build.BRAND.equals("lava", ignoreCase = true)
    val isAndroid12OrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

    var deleteDialog by remember { mutableStateOf(false) }

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

    var isNotificationOn by remember {
        mutableStateOf(NotificationManagerCompat.from(context).areNotificationsEnabled())
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                // Update notification state when app resumes
                isNotificationOn = NotificationManagerCompat.from(context).areNotificationsEnabled()
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ ->
        isNotificationOn = NotificationManagerCompat.from(context).areNotificationsEnabled()
    }


    var isReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isReady = true
    }

    val composition1 by rememberLottieComposition(
        spec = LottieCompositionSpec.RawRes(R.raw.heart)
    )

    val progress1 by animateLottieCompositionAsState(
        composition = composition1,
        iterations = LottieConstants.IterateForever,
        isPlaying = true,
        restartOnPlay = false
    )

    if (deleteDialog){
        DeleteDialog(
            onConfirm = {
                viewModel.resetSubjects()
                viewModel4.resetTimetable()
            },
            onDismiss = {
                deleteDialog = false },
            containerColor = contentColor,
            text = "RESET",
            toast = "ALL RECORDS DELETED!!",
            isAndroid12OrAbove = isAndroid12OrAbove
        )
    }

    if (!isReady) {
        Box(modifier = Modifier.fillMaxSize().background(Color.Transparent), contentAlignment = Alignment.Center) {
            Text(text = "LOADING...", fontSize = 42.sp, fontFamily = nothingFontFamily, fontWeight = FontWeight.Bold, color = contentColor)        }
    } else {
        Scaffold(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && deleteDialog) {
                        renderEffect = BlurEffect(radiusX = 10.dp.toPx(), radiusY = 10.dp.toPx())
                    }
                },
            bottomBar = {
                Column (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .heightIn(min = 30.dp)
                        .windowInsetsPadding(WindowInsets.navigationBars),
                    horizontalAlignment = Alignment.CenterHorizontally
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
                            3
                        )
                    }

                }
            }
        ) { _ ->

                Column(
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
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 130.dp, start = 10.dp, end = 10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeightIn(50.dp,60.dp)
                            .shadow(15.dp, shape = RoundedCornerShape(50))
                            .clip(RoundedCornerShape(50))
                            .background(contentColor)
                    ){
                        Row (
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(
                                text = "NOTIFICATIONS",
                                fontFamily = nothingFontFamily,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.weight(weight = 1f))
                            if(isAndroid12OrAbove) {
                                NotificationToggle(
                                    contentColor,
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(0.5f),
                                    backgroundColor.copy(0.8f),
                                    isSwitchOn = isNotificationOn,
                                    onToggleChange = {
                                        if (isNotificationOn) {
                                            // Open settings to turn OFF
                                            val intent =
                                                Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                                                    putExtra(
                                                        Settings.EXTRA_APP_PACKAGE,
                                                        context.packageName
                                                    )
                                                }
                                            context.startActivity(intent)
                                        } else {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                                if (ActivityCompat.shouldShowRequestPermissionRationale(
                                                        activity,
                                                        Manifest.permission.POST_NOTIFICATIONS
                                                    )
                                                ) {
                                                    // Show system permission dialog again
                                                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                                                } else {
                                                    // Already denied once, redirect to settings
                                                    val intent =
                                                        Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                            data = Uri.fromParts(
                                                                "package",
                                                                context.packageName,
                                                                null
                                                            )
                                                        }
                                                    context.startActivity(intent)
                                                }
                                            }
                                        }
                                    }
                                )
                            }else{
                                NotificationToggle(
                                    contentColor,
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.primary.copy(0.5f),
                                    backgroundColor.copy(0.8f),
                                    isSwitchOn = isNotificationOn,
                                    onToggleChange = {
                                        if (isNotificationOn) {
                                            // Open settings to turn OFF
                                            val intent = Intent().apply {
                                                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                                                putExtra(
                                                    Settings.EXTRA_APP_PACKAGE,
                                                    context.packageName
                                                )
                                            }
                                            context.startActivity(intent)
                                        } else {

                                            val intent = Intent().apply {
                                                action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
                                                putExtra(
                                                    Settings.EXTRA_APP_PACKAGE,
                                                    context.packageName
                                                )
                                            }
                                            context.startActivity(intent)
                                        }
                                    }
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeightIn(50.dp,60.dp)
                            .shadow(15.dp, shape = RoundedCornerShape(50))
                            .clip(RoundedCornerShape(50))
                            .background(contentColor)
                    ){
                        Row (
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ){
                            Text(
                                text = "CHANGE THEME",
                                fontFamily = nothingFontFamily,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 18.sp
                            )
                            ThemeSwitch(
                                contentColor,
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.primary.copy(0.5f),
                                backgroundColor.copy(0.8f),
                                userPreferences = UserPreferences(context)
                            )

                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeightIn(50.dp,60.dp)
                            .shadow(15.dp, shape = RoundedCornerShape(50))
                            .clip(RoundedCornerShape(50))
                            .background(contentColor)
                            .clickable {
                                val intent = Intent(Intent.ACTION_SENDTO).apply {
                                    data = Uri.parse("mailto:ironheartx09@gmail.com")
                                    putExtra(Intent.EXTRA_SUBJECT, "Bug Report")
                                    putExtra(Intent.EXTRA_TEXT, "Describe your issue here...")
                                }
                                context.startActivity(intent)
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "REPORT BUG",
                                fontFamily = nothingFontFamily,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 18.sp
                            )
                            IconButton(
                                onClick = {
                                    val intent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:ironheartx09@gmail.com")
                                        putExtra(Intent.EXTRA_SUBJECT, "Bug Report")
                                        putExtra(Intent.EXTRA_TEXT, "Describe your issue here...")
                                    }
                                    context.startActivity(intent)
                                },
                                modifier = Modifier.size(42.dp),
                            ) {
                                Icon(
                                    painter = painterResource(R.drawable.bug),
                                    contentDescription = "Reset Attendance",
                                    modifier = Modifier.size(36.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeightIn(50.dp,60.dp)
                            .shadow(15.dp, shape = RoundedCornerShape(50))
                            .clip(RoundedCornerShape(50))
                            .background(contentColor)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SHARE APP",
                                fontFamily = nothingFontFamily,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 18.sp
                            )
                            IconButton(
                                onClick = {},
                                modifier = Modifier.size(42.dp),
                            ) {
                                Icon(
                                    Icons.Default.Share,
                                    contentDescription = "Reset Attendance",
                                    modifier = Modifier.size(36.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .requiredHeightIn(50.dp,60.dp)
                            .shadow(15.dp, shape = RoundedCornerShape(50))
                            .clip(RoundedCornerShape(50))
                            .background(contentColor)
                            .clickable{
                                deleteDialog = true
                            }
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "RESET APP",
                                fontFamily = nothingFontFamily,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 18.sp
                            )
                            IconButton(
                                onClick = {
                                    deleteDialog = true
                                },
                                modifier = Modifier.size(42.dp),
                            ) {
                                Icon(
                                    Icons.Default.Refresh,
                                    contentDescription = "Reset Attendance",
                                    modifier = Modifier.size(36.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        }
                    }
                    Column (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding() + 20.dp, top = 80.dp),
                        horizontalAlignment = Alignment.CenterHorizontally)
                    {
                        Row{
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

