package com.example.attendease.screen

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.attendease.ui.theme.nothingFontFamily
import com.example.attendease.ui.theme.roundFontFamily
import com.example.attendease.uicomponent.TimeBasedGreeting
import com.example.attendease.uicomponent.bottomnavbar.BottomNavNoAnimation
import com.example.attendease.uicomponent.bottomnavbar.Screen
import kotlinx.coroutines.delay

@Composable
fun DonateScreen(
    selectedColor: Int?,
    navController: NavController = rememberNavController()
) {
    val screen = listOf(
        Screen.Home,
        Screen.Timetable,
        Screen.Donate,
        Screen.Settings
    )

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

    var isReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(100)
        isReady = true
    }

    if (!isReady) {
        Box(modifier = Modifier.fillMaxSize().background(contentColor), contentAlignment = Alignment.Center) {
            Text(text = "LOADING...", fontSize = 42.sp, fontFamily = nothingFontFamily, fontWeight = FontWeight.Bold, color = contentColor)        }
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
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
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(backgroundColor),

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
                            fontFamily = roundFontFamily
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 80.dp, start = 10.dp, end = 10.dp)
                ) {

                    Column(
                        modifier = Modifier
                            .padding(top = 10.dp, bottom = 80.dp)
                            .fillMaxSize()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 10.dp),
                        ) {
                            Text(
                                text = "Donate US",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 28.sp,
                                fontFamily = nothingFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                modifier = Modifier.alpha(0.6f)
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(start = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Text(
                                    text = "Under Development",
                                    color = MaterialTheme.colorScheme.tertiary,
                                    fontSize = 28.sp,
                                    fontFamily = nothingFontFamily,
                                    fontWeight = FontWeight.ExtraBold,

                                    )


                            }
                        }
                        TimeBasedGreeting()
                        Text(
                            text = "In Construction",
                            color = MaterialTheme.colorScheme.secondary, // Lighter color
                            fontSize = 12.sp,
                            fontFamily = roundFontFamily,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.alpha(0.5f)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 600.dp)// Height based on content
                                .padding(top = 25.dp, bottom = 30.dp)
                                .clip(
                                    RoundedCornerShape(20.dp)
                                )
                                .background(contentColor.copy(alpha = 0.9f))

                                .weight(1f)
                        ) {


                        }


                    }
                }
            }
        }
    }
}