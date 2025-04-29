package com.example.attendease.screen

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.attendease.UserPreferences
import com.example.attendease.dailogbox.AddClass
import com.example.attendease.dailogbox.EditClass
import com.example.attendease.model.SubjectViewModel
import com.example.attendease.model.TimetableViewModel
import com.example.attendease.notification.AlarmScheduler
import com.example.attendease.timetabledata.Timetable
import com.example.attendease.ui.theme.ThemePreference
import com.example.attendease.ui.theme.nothingFontFamily
import com.example.attendease.ui.theme.roundFontFamily
import com.example.attendease.uicomponent.TimeTableItem
import com.example.attendease.uicomponent.Weekly
import com.example.attendease.uicomponent.bottomnavbar.BottomNavNoAnimation
import com.example.attendease.uicomponent.bottomnavbar.Screen
import kotlinx.coroutines.delay
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun TimeTableScreen(
    selectedColor: Int?,
    navController: NavController = rememberNavController(),
    timeViewModel: TimetableViewModel,
    subjectViewModel: SubjectViewModel,
    userPreference: UserPreferences
) {
    val themePref by userPreference.themePreferenceFlow.collectAsState(initial = ThemePreference.LIGHT)
    val isDark = when (themePref) {
        ThemePreference.DARK -> true
        ThemePreference.LIGHT -> false
        ThemePreference.SYSTEM_DEFAULT -> isSystemInDarkTheme()
    }
    val context = LocalContext.current
    var addClassDialog by remember{ mutableStateOf(false) }
    var editClassDialog by remember { mutableStateOf(false) }
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
        if(isDark)
            MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
    }else {
        if(isDark)MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.onPrimary
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
    var selectedDay by remember { mutableStateOf("") }
    var isReady by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        selectedDay = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase()
        timeViewModel.loadClasses(selectedDay)
        delay(100)
        isReady = true
    }
    var selectedClass by remember { mutableStateOf<Timetable?>(null) }
    var subName by remember { mutableStateOf("") }
    var startTime by remember { mutableStateOf("12:00 AM") }
    var endTime by remember { mutableStateOf("12:00 AM") }
    fun resetFields(){
        subName =""
        selectedDay = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase()
        startTime = "12:00 AM"
        endTime = "12:00 AM"
    }
    fun parseTimeTo24Hour(time: String): LocalTime {
        val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
        return LocalTime.parse(time, formatter)
    }

    val alarmScheduler = remember {
        AlarmScheduler(context)
    }


    if (addClassDialog) {
        AddClass(
            subName = subName,
            onSubNameChange = { subName = it.uppercase() },
            day = selectedDay,
            onDayChanged = { selectedDay = it.uppercase() },
            startTime = startTime,
            onStartTimeChanged = { startTime = it },
            endTime = endTime,
            onEndTimeChanged = {endTime = it},
            onDismiss = { addClassDialog = false },
            onConfirm = {
                val start = parseTimeTo24Hour(startTime)
                val end = parseTimeTo24Hour(endTime)
                val firstTime = startTime
                val lastTime = endTime
                if(subName.isNotEmpty() && start.isBefore(end)){
                    timeViewModel.addClass(subName,selectedDay, firstTime, lastTime)
                    addClassDialog=false
                    alarmScheduler.scheduleAllAlarms()
                    resetFields()
                }else{
                    Toast.makeText(context,"Enter proper details", Toast.LENGTH_SHORT).show()
                    addClassDialog=true
                }

            },
            containerColor = contentColor,
            viewModel = subjectViewModel
        )
    }
    if (editClassDialog && selectedClass !=null) {
        EditClass(
            subName = subName,
            onSubNameChange = { subName = it.uppercase() },
            day = selectedDay,
            onDayChanged = { selectedDay = it.uppercase() },
            startTime = startTime,
            onStartTimeChanged = { startTime = it },
            endTime = endTime,
            onEndTimeChanged = {endTime = it},
            onDismiss = {
                resetFields()
                editClassDialog = false },
            onUpdate = {
                val start = parseTimeTo24Hour(startTime)
                val end = parseTimeTo24Hour(endTime)
                val firstTime = startTime
                val lastTime = endTime
                if(subName.isNotEmpty() && start.isBefore(end)){
                    selectedClass?.let { timetable ->
                        timeViewModel.updateClass(
                            id = timetable.Id,
                            className = subName,
                            day = selectedDay,
                            startTime = firstTime,
                            endTime = lastTime
                        )
                        alarmScheduler.scheduleAllAlarms()
                        resetFields()
                        editClassDialog=false
                    }
                }else{
                    Toast.makeText(context,"Enter proper details", Toast.LENGTH_SHORT).show()
                }
                resetFields()
                editClassDialog=false

            },
            containerColor = contentColor,
            viewModel = subjectViewModel
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && (addClassDialog || editClassDialog)) {
                        renderEffect = BlurEffect(radiusX = 10.dp.toPx(), radiusY = 10.dp.toPx())
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
                            if(isLava && isDark) MaterialTheme.colorScheme.onPrimaryContainer.copy(0.5f)
                            else{
                                if(isDark) MaterialTheme.colorScheme.onPrimary.copy(0.5f)
                                else backgroundColor.copy(alpha = 0.5f)},
                            navController,
                            1
                        )
                    }
                }
            }
        ) { _ ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 30.dp)
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .clip(RoundedCornerShape(50))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
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
                        .windowInsetsPadding(WindowInsets.statusBars)
                        .padding(top = 50.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = 10.dp, bottom = 100.dp)
                            .fillMaxSize()
                    ) {
                        Weekly(
                            modifier = Modifier,
                            onDateSelected = {day->
                                val fullDay = timeViewModel.getFullDay(day.uppercase())
                                timeViewModel.setSelectedDay(fullDay)
                            },
                        )

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .heightIn(min = 500.dp)
                                .padding(top = 25.dp, bottom = 30.dp, start = 10.dp, end = 10.dp)
                                .clip(RoundedCornerShape(20.dp))
                                .background(contentColor.copy(alpha = 0.9f))
                        ) {
                            val timeTableList = timeViewModel.classesForDate.collectAsState().value
                            if(timeTableList.isEmpty()){
                                Text(
                                    text = "Click the + icon to add classes",
                                    fontSize = 22.sp,
                                    fontFamily = nothingFontFamily,
                                    color = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.align(Alignment.Center)
                                )
                            }else {
                                LazyColumn(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 2.dp, vertical = 2.dp)
                                ) {
                                    items(timeTableList) { item ->
                                        TimeTableItem(
                                            timetable = item,
                                            onDelete = { timeViewModel.deleteClass(item)
                                                alarmScheduler.scheduleAllAlarms()
                                            },
                                            onEdit = {
                                                selectedClass = item
                                                subName = item.subjectName
                                                selectedDay = item.day
                                                startTime = item.startTime
                                                endTime = item.endTime
                                                editClassDialog = true
                                            },
                                            onClick = { /* TODO: Navigate to detail screen or show bottom sheet */ },
                                            backgroundColor = if(isDark && !isLava)MaterialTheme.colorScheme.tertiaryContainer else backgroundColor,
                                            contentColor
                                        )
                                    }
                                }
                            }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(30.dp)
                                    .align(Alignment.BottomCenter)
                                    .drawWithCache {
                                        onDrawBehind {
                                            // Draw Background Gradient
                                            drawRect(
                                                Brush.linearGradient(
                                                    colors = listOf(
                                                        contentColor,
                                                        Color.Transparent
                                                    ),
                                                    start = Offset(0f, size.height),
                                                    end = Offset(0f, 0f)
                                                )
                                            )


                                        }
                                    }
                            ) { }
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(30.dp)
                                    .align(Alignment.TopCenter)
                                    .drawWithCache {
                                        onDrawBehind {
                                            // Draw Background Gradient
                                            drawRect(
                                                Brush.linearGradient(
                                                    colors = listOf(
                                                        Color.Transparent,
                                                        contentColor
                                                    ),
                                                    start = Offset(0f, size.height),
                                                    end = Offset(0f, 0f)
                                                )
                                            )


                                        }
                                    }
                            ) { }

                            IconButton(
                                onClick = { addClassDialog=true },
                                colors = IconButtonDefaults.iconButtonColors(
                                    containerColor = if (isAndroid12OrAbove) {
                                        MaterialTheme.colorScheme.onTertiary
                                    } else {
                                        Color.White.copy(alpha = 0.8f)
                                    }
                                ),
                                modifier = Modifier
                                    .padding(20.dp)
                                    .size(46.dp)
                                    .align(Alignment.BottomEnd)
                                    .shadow(
                                        elevation = 5.dp,
                                        shape = RoundedCornerShape(50.dp),
                                        ambientColor = Color.Black,
                                        spotColor = Color.Black
                                    )
                            ) {

                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Add Subject",
                                    tint = if (isAndroid12OrAbove) {
                                        MaterialTheme.colorScheme.secondary
                                    } else {
                                        selectColor
                                    },
                                    modifier = Modifier.size(46.dp)
                                )
                            }
                        }
                    }
                }
        }
    }
}
