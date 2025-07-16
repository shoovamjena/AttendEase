package com.ironheartproduction.attendelite.screen

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.ironheartproduction.attendelite.UserPreferences
import com.ironheartproduction.attendelite.dailogbox.DeleteDialog
import com.ironheartproduction.attendelite.dailogbox.EditAttendanceDialog
import com.ironheartproduction.attendelite.viewmodel.DetailViewModel
import com.ironheartproduction.attendelite.ui.theme.ThemePreference
import com.ironheartproduction.attendelite.ui.theme.coinyFontFamily
import com.ironheartproduction.attendelite.ui.theme.nothingFontFamily
import com.ironheartproduction.attendelite.ui.theme.roundFontFamily
import com.ironheartproduction.attendelite.uicomponent.AdaptiveText
import com.ironheartproduction.attendelite.uicomponent.AnimatedBackButton
import com.ironheartproduction.attendelite.uicomponent.AttendanceItemDetailed
import com.ironheartproduction.attendelite.uicomponent.bottomnavbar.Screen
import kotlinx.coroutines.delay

@Composable
fun AttendanceDetailExpanded(
    subject: String,
    navController: NavController,
    viewModel: DetailViewModel,
    userPreference: UserPreferences
){
    var editDialog by remember { mutableStateOf(false) }
    var deleteDialog by remember { mutableStateOf(false) }
    var attendanceIdToChange by remember { mutableStateOf<Int?>(null) }
    var subjectId by remember { mutableStateOf<Int?>(null) }
    var currentStatus by remember { mutableStateOf("") }

    val themePref by userPreference.themePreferenceFlow.collectAsState(initial = ThemePreference.LIGHT)
    val isDark = when (themePref) {
        ThemePreference.DARK -> true
        ThemePreference.LIGHT -> false
        ThemePreference.SYSTEM_DEFAULT -> isSystemInDarkTheme()
    }
    val attendanceRecords by viewModel.attendanceRecords.collectAsState()
    val isLava = Build.BRAND.equals("lava", ignoreCase = true)
    val isAndroid12OrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val backgroundColor =if(isLava){
        if(isDark)
            MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
    }else {
        if(isDark)MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.onPrimary
    }
    val contentColor = if (isLava){
        MaterialTheme.colorScheme.onPrimary
    }else {
        MaterialTheme.colorScheme.primaryContainer
    }
    if(deleteDialog){
        DeleteDialog(
            onDismiss = { deleteDialog = false},
            onConfirm = { viewModel.deleteDetail(attendanceIdToChange!!, subjectId!!,currentStatus)
                        deleteDialog = false},
            containerColor = contentColor,
            text = "Attendance for $subject",
            toast = "Attendance for $subject is deleted",
            isAndroid12OrAbove = isAndroid12OrAbove
        )
    }
    LaunchedEffect(attendanceRecords) {
        if (attendanceRecords.isEmpty()) {
            delay(1500)
            navController.navigate(Screen.Home.route) {
                popUpTo(0) {
                    inclusive = true
                }
                launchSingleTop = true
            }
        }
    }

    if(editDialog){
        EditAttendanceDialog(
            onDismiss = { editDialog = false },
            containerColor = contentColor,
            onPresent = {
                if (currentStatus == "Absent") {
                    viewModel.updateDetail(subjectId!!,attendanceIdToChange!!,"Present")
                }
                editDialog = false
            },
            onAbsent = {
                if (currentStatus == "Present") {
                    viewModel.updateDetail(subjectId!!,attendanceIdToChange!!,"Absent")
                }
                editDialog = false
            },
            status = currentStatus,
            isAndroid12OrAbove = isAndroid12OrAbove
        )
    }
    var isReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(500)
        isReady = true
    }
    if (!isReady) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor), contentAlignment = Alignment.Center) {
            Text(text = "LOADING...", fontSize = 42.sp, fontFamily = nothingFontFamily, fontWeight = FontWeight.Bold, color = contentColor)        }
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize()
                .graphicsLayer {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && (editDialog || deleteDialog)) {
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
                        AnimatedBackButton(navController = navController, contentColor)
                    }
                }
            }
        ) { _ ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .heightIn(min = 30.dp)
                    .windowInsetsPadding(WindowInsets.statusBars)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ATTENDELITE",
                        color = contentColor,
                        fontSize = 38.sp,
                        textAlign = TextAlign.Center,
                        fontFamily = roundFontFamily
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                        .heightIn(min = 500.dp, max = 650.dp)
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(top = 70.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Attendance Details of",
                        fontSize = 24.sp,
                        fontFamily = coinyFontFamily,
                        color = MaterialTheme.colorScheme.tertiaryContainer
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    AdaptiveText(subject,32,3,10,modifier = Modifier.padding(top = 10.dp, start = 20.dp),
                        nothingFontFamily)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(RoundedCornerShape(40.dp))
                            .background(contentColor.copy(0.5f))
                    ) {
                        if(attendanceRecords.isEmpty()){
                            Text(
                                text = "NO RECORDS FOUND!!\n\nHeading Back To Home",
                                fontFamily = nothingFontFamily,
                                fontSize = 24.sp,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        }
                        LazyColumn {
                            items(attendanceRecords) { record ->
                                AttendanceItemDetailed(
                                    record,
                                    onDelete = {
                                        currentStatus = record.status
                                        attendanceIdToChange = record.attendId
                                        subjectId = record.id
                                        deleteDialog = true
                                    },
                                    onEdit = {
                                        currentStatus = record.status
                                        attendanceIdToChange = record.attendId
                                        subjectId = record.id
                                        editDialog = true
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}