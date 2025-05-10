package com.example.attendease.screen

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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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
import com.example.attendease.UserPreferences
import com.example.attendease.dailogbox.AddSubject
import com.example.attendease.dailogbox.AttendanceDialog
import com.example.attendease.dailogbox.ChangeTargetDialog
import com.example.attendease.dailogbox.EditSubject
import com.example.attendease.dailogbox.ResetDialog
import com.example.attendease.model.DetailViewModel
import com.example.attendease.model.MainViewModel
import com.example.attendease.model.SubjectViewModel
import com.example.attendease.model.TimetableViewModel
import com.example.attendease.subjectdata.Subject
import com.example.attendease.ui.theme.ThemePreference
import com.example.attendease.ui.theme.nothingFontFamily
import com.example.attendease.ui.theme.roundFontFamily
import com.example.attendease.uicomponent.SplashTransition
import com.example.attendease.uicomponent.SubjectItem
import com.example.attendease.uicomponent.TimeBasedGreeting
import com.example.attendease.uicomponent.bottomnavbar.BottomNavNoAnimation
import com.example.attendease.uicomponent.bottomnavbar.Screen
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun HomeScreen(
    userName: String,
    viewModel: SubjectViewModel,
    viewModel2: DetailViewModel,
    viewModel3: MainViewModel,
    navController: NavController,
    viewModel4: TimetableViewModel,
    userPreference: UserPreferences
) {
    val subjects by viewModel.subjects.collectAsState()

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
    val userPreferences = remember { UserPreferences(context) }
    val coroutineScope = rememberCoroutineScope()

    val isLava = Build.BRAND.equals("lava", ignoreCase = true)
    var resetDialog by remember { mutableStateOf(false) }
    var addSubjectDialog by rememberSaveable  { mutableStateOf(false) }
    var showChangeTargetDialog by remember { mutableStateOf(false) }
    var editSubjectDialog by rememberSaveable { mutableStateOf(false) }
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    var attendDetail by remember { mutableStateOf<Subject?>(null) }
    var attendanceDialog by rememberSaveable { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    var total by remember { mutableStateOf("") }
    var attend by remember { mutableStateOf("") }
    val isAndroid12OrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val backgroundColor = if(isLava){
        if(isDark)
        MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
    }else {
        if(isDark)MaterialTheme.colorScheme.onSecondary else MaterialTheme.colorScheme.onPrimary
    }
    val contentColor = if (isLava){
        MaterialTheme.colorScheme.onPrimary
    }else {
        MaterialTheme.colorScheme.primaryContainer
    }

    var expanded by remember { mutableStateOf(false) }

    fun resetFields() {
        text = ""
        total = ""
        attend = ""
    }
    if (addSubjectDialog) {
        AddSubject(
            subName = text,
            onSubNameChange = { text = it.uppercase() },
            totalClasses = total,
            onTotalClassesChange = { total = it },
            classesAttended = attend,
            onClassesAttendChange = { attend = it },
            onDismiss = {
                resetFields()
                addSubjectDialog = false },
            onConfirm = {
                val attended = attend.toIntOrNull()
                val all = total.toIntOrNull()
                when {
                    text.isEmpty() -> {
                        Toast.makeText(context, "Subject can't be empty", Toast.LENGTH_SHORT).show()
                    }
                    attended == null -> {
                        Toast.makeText(context, "Attended classes can't be empty", Toast.LENGTH_SHORT).show()
                    }
                    all == null -> {
                        Toast.makeText(context, "Total classes can't be empty", Toast.LENGTH_SHORT).show()
                    }
                    attended < 0 || all < 0 -> {
                        Toast.makeText(context, "Classes can't be negative", Toast.LENGTH_SHORT).show()
                    }
                    all < attended -> {
                        Toast.makeText(context, "Total classes can't be less than classes attended", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        viewModel.addSubject(name = text, attend = attended, total = all)
                        addSubjectDialog = false
                        resetFields()
                    }
                }

            },
            containerColor = contentColor,
            isAndroid12OrAbove = isAndroid12OrAbove
        )
    }

    if (editSubjectDialog && selectedSubject!=null) {
        EditSubject(
            subName = text,
            onSubNameChange = { text = it.uppercase() },
            classesAttended = attend,
            onClassesAttendChange = { attend = it },
            totalClasses = total,
            onTotalClassesChange = { total = it },
            onDismiss = {
                resetFields()
                editSubjectDialog = false
            },
            onConfirm = {
                val attended = attend.toIntOrNull()
                val all = total.toIntOrNull()
                when {
                    text.isEmpty() -> {
                        Toast.makeText(context, "Subject can't be empty", Toast.LENGTH_SHORT).show()
                    }
                    attended == null -> {
                        Toast.makeText(context, "Attended classes can't be empty", Toast.LENGTH_SHORT).show()
                    }
                    all == null -> {
                        Toast.makeText(context, "Total classes can't be empty", Toast.LENGTH_SHORT).show()
                    }
                    attended < 0 || all < 0 -> {
                        Toast.makeText(context, "Classes can't be negative", Toast.LENGTH_SHORT).show()
                    }
                    all < attended -> {
                        Toast.makeText(context, "Total classes can't be less than classes attended", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        selectedSubject?.let { subject -> // Null safety check
                            viewModel.updateSubject(
                                id = subject.id,
                                newName = text,
                                newAttend = attended,
                                newTotal = all
                            )
                            viewModel2.resetAttendance(subject)
                            editSubjectDialog = false
                            resetFields()
                        }
                    }
                }

            },
            contentColor,
            isAndroid12OrAbove = isAndroid12OrAbove
        )
    }

    val currentTarget by userPreferences.targetFlow.collectAsState(initial = 75f)
    if (showChangeTargetDialog) {
        ChangeTargetDialog(
            contentColor = contentColor,
            currentTarget = currentTarget,
            onDismiss = { showChangeTargetDialog = false },
            onConfirm = { newTarget ->
                coroutineScope.launch {
                    userPreferences.saveTarget(context,newTarget.toFloat())
                }
                showChangeTargetDialog = false
            },
            isAndroid12OrAbove = isAndroid12OrAbove
        )
    }

    if (resetDialog){
        ResetDialog(
            onConfirm = {
                viewModel.resetSubjects()
                viewModel4.resetTimetable()
            },
            onDismiss = {
                resetDialog = false
                expanded = false},
            containerColor = contentColor,
            toast = "ALL RECORDS DELETED!!",
            isAndroid12OrAbove = isAndroid12OrAbove
        )
    }
    if (!viewModel3.hasSplashPlayed) {
        SplashTransition(
            onAnimationEnd = {
                viewModel3.hasSplashPlayed = true
            }, backgroundColor,contentColor
        )
    } else {
        var isReady by remember { mutableStateOf(false) }
        LaunchedEffect(Unit) {
            delay(100)
            isReady = true
        }
        if (!isReady) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(contentColor), contentAlignment = Alignment.Center) {}
        }
        else {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && (resetDialog || showChangeTargetDialog ||
                                    attendanceDialog || editSubjectDialog || addSubjectDialog)) {
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
                                    if(isDark) MaterialTheme.colorScheme.primary.copy(0.5f)
                                    else backgroundColor.copy(alpha = 0.5f)},
                                navController,
                                0
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
                            .windowInsetsPadding(WindowInsets.statusBars)
                            .padding(top = 50.dp, start = 10.dp, end = 10.dp)
                    ) {

                        Column(
                            modifier = Modifier
                                .padding(top = 10.dp, bottom = 100.dp)
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp),
                            ) {
                                Text(
                                    text = "Hello",
                                    color =MaterialTheme.colorScheme.primary,
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
                                        text = userName,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        fontSize = 28.sp,
                                        fontFamily = nothingFontFamily,
                                        fontWeight = FontWeight.ExtraBold,

                                        )
                                    Box(modifier = Modifier.shadow(5.dp, shape = RoundedCornerShape(50))) {

                                        IconButton(
                                            onClick = { expanded = true },
                                            colors = IconButtonDefaults.iconButtonColors(
                                                if(!isDark) contentColor else MaterialTheme.colorScheme.primary.copy(0.7f)
                                            ),
                                            modifier = Modifier.size(32.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Menu,
                                                contentDescription = "More Actions",
                                                tint = backgroundColor
                                            )
                                        }
                                        DropdownMenu(
                                            expanded = expanded,
                                            onDismissRequest = { expanded = false },
                                            modifier = Modifier
                                                .background(backgroundColor.copy(alpha = 0.5f))
                                        ) {
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = "Change Target",
                                                        fontSize = 14.sp
                                                    )
                                                },
                                                leadingIcon = {
                                                    Icon(
                                                        Icons.Default.Create, // or any icon you like
                                                        contentDescription = "Change Target",
                                                        modifier = Modifier.size(22.dp)
                                                    )
                                                },
                                                onClick = {
                                                    showChangeTargetDialog = true
                                                    expanded = false
                                                },
                                            )
                                            DropdownMenuItem(
                                                text = {
                                                    Text(
                                                        text = "Reset",
                                                        fontSize = 14.sp
                                                    )
                                                },
                                                leadingIcon = {
                                                    Icon(
                                                        Icons.Default.Refresh,
                                                        contentDescription = "Reset",
                                                        modifier = Modifier.size(22.dp)
                                                    )
                                                },
                                                onClick = {
                                                    if (subjects.isNotEmpty()){
                                                    resetDialog = true}
                                                    else{
                                                        Toast.makeText(context,"No records to delete !!", Toast.LENGTH_LONG).show()
                                                        expanded = false
                                                    }
                                                },
                                            )

                                        }
                                    }
                                }
                            }
                            TimeBasedGreeting()
                            Text(
                                text = "Let's Keep Your Attendance on Point!",
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 12.sp,
                                fontFamily = roundFontFamily,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.alpha(0.5f)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 400.dp, max = 650.dp)
                                    .padding(top = 25.dp, bottom = 30.dp)
                                    .shadow(10.dp, shape = RoundedCornerShape(20.dp))
                                    .clip(
                                        RoundedCornerShape(20.dp)
                                    )
                                    .background(contentColor.copy(alpha = 0.9f))

                                    .weight(1f)
                            ) {
                                val listState = rememberLazyListState()

                                if (subjects.isEmpty()) {
                                    Text(
                                        text = "Click the + icon to add subject",
                                        fontSize = 22.sp,
                                        fontFamily = nothingFontFamily,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.align(Alignment.Center)
                                    )
                                }

                                LazyColumn(
                                    state = listState,
                                    modifier = Modifier.padding(3.dp)
                                ) {
                                    items(subjects, key = { it.id }) { subject ->
                                        SubjectItem(
                                            subject, onPresent = { viewModel.markPresent(subject) },
                                            onAbsent = { viewModel.markAbsent(subject) },
                                            onDelete = {
                                                viewModel.deleteSubject(subject)
                                                viewModel4.deleteClassBasedOnSubject(subject)
                                            },
                                            onEdit = {
                                                selectedSubject =
                                                    subject // Set the selected subject
                                                text = subject.name
                                                attend = subject.attend.toString()
                                                total = subject.total.toString()
                                                editSubjectDialog = true
                                            },
                                            onReset = {
                                                viewModel.resetAttendance(subject)
                                                viewModel2.resetAttendance(subject)
                                            },
                                            onClick = {
                                                attendDetail = subject
                                                viewModel2.getAttendanceRecords(subject.id)
                                                attendanceDialog = true
                                            },
                                            viewModel = viewModel2,
                                            backgroundColor = backgroundColor,
                                            dialogColor = contentColor
                                        )
                                    }
                                }
                                attendDetail?.let { subject ->
                                    AttendanceDialog(
                                        subject = subject,
                                        viewModel = viewModel2,
                                        onDismiss = {
                                            attendDetail = null
                                            attendanceDialog = false },
                                        contentColor,
                                        backgroundColor,
                                        navController,
                                        isAndroid12OrAbove = isAndroid12OrAbove
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(35.dp)
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
                                        .height(35.dp)
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
                                    onClick = { addSubjectDialog = true },
                                    colors = IconButtonDefaults.iconButtonColors(
                                        containerColor = if(isDark) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.tertiaryContainer
                                    ),
                                    modifier = Modifier
                                        .padding(10.dp)
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
                                        tint = if(isDark) Color.White else MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(46.dp)
                                    )
                                }
                            }
                        }
                    }
            }
        }
    }
}