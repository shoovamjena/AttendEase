

package com.example.attendease.screen

import android.os.Build
import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
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
import com.example.attendease.model.DetailViewModel
import com.example.attendease.model.MainViewModel
import com.example.attendease.model.SubjectViewModel
import com.example.attendease.subjectdata.Subject
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
    selectedColor: Int?,
    viewModel: SubjectViewModel,
    viewModel2: DetailViewModel,
    viewModel3: MainViewModel,
    navController: NavController
) {
    val screen = listOf(
        Screen.Home,
        Screen.Timetable,
        Screen.Donate,
        Screen.Settings
    )

    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val coroutineScope = rememberCoroutineScope()

    val isLava = Build.BRAND.equals("lava", ignoreCase = true)
    var addSubjectDialog by rememberSaveable  { mutableStateOf(false) }
    var showChangeTargetDialog by remember { mutableStateOf(false) }
    var editSubjectDialog by rememberSaveable { mutableStateOf(false) }
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    var attendDetail by remember { mutableStateOf<Subject?>(null) }
    var attendanceDialog by rememberSaveable { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    var total by remember { mutableStateOf("") }
    var attend by remember { mutableStateOf("") }
    val selectColor = selectedColor?.let { Color(it) } ?: MaterialTheme.colorScheme.primary
    val isAndroid12OrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    var showColorScreen by remember { mutableStateOf(false) }
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
                if(text.isNotEmpty() && attended != null && all != null && all>=attended){
                    viewModel.addSubject(name = text, attend = attended, total = all)
                    addSubjectDialog = false
                    resetFields()
                }else{
                    if(text.isEmpty()){
                        Toast.makeText(context,"Subject can't be empty",Toast.LENGTH_SHORT).show()
                    }else if(attended ==null){
                        Toast.makeText(context,"Attended classes can't be empty",Toast.LENGTH_SHORT).show()
                    }else if(all == null){
                        Toast.makeText(context,"Total classes can't be empty",Toast.LENGTH_SHORT).show()
                    }else if(all<attended){
                        Toast.makeText(context,"Total classes can't be less than classes attended",Toast.LENGTH_SHORT).show()
                    }

                }

            },
            containerColor = contentColor
        )
    }

    if (editSubjectDialog && selectedSubject!=null) {
        EditSubject(
            subName = text,
            onSubNameChange = { text = it.uppercase() },
            classesAtended = attend,
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
                if (text.isNotEmpty() && attended != null && all != null) {
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
            },
            contentColor

        )
    }

    val currentTarget by userPreferences.targetFlow.collectAsState(initial = 75f)
    if (showChangeTargetDialog) {
        ChangeTargetDialog(
            currentTarget = currentTarget,
            onDismiss = { showChangeTargetDialog = false },
            onConfirm = { newTarget ->
                coroutineScope.launch {
                    userPreferences.saveTarget(context,newTarget.toFloat())
                }
                showChangeTargetDialog = false
            }
        )
    }

    if (showColorScreen) {
        ChangeColorDialog(
            onDismiss = { showColorScreen = false },
            onColourSelected = { selectedColor ->
                coroutineScope.launch {
                    userPreferences.saveThemeColor(selectedColor.toArgb())
                }
            },
        )
    }

    if (!viewModel3.hasSplashPlayed) {
        SplashTransition(
            onAnimationEnd = {
                viewModel3.hasSplashPlayed = true
            },backgroundColor,contentColor
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
            Box(modifier = Modifier.fillMaxSize().background(contentColor), contentAlignment = Alignment.Center) {}
        }
        else {
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
                                backgroundColor.copy(alpha = 0.7f),
                                navController,
                                0
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
                                        text = userName,
                                        color = MaterialTheme.colorScheme.tertiary,
                                        fontSize = 28.sp,
                                        fontFamily = nothingFontFamily,
                                        fontWeight = FontWeight.ExtraBold,

                                        )
                                    Box {

                                        IconButton(
                                            onClick = { expanded = true },
                                            colors = IconButtonDefaults.iconButtonColors(
                                                contentColor
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
                                            if (!isAndroid12OrAbove) {
                                                DropdownMenuItem(
                                                    text = {
                                                        Text(
                                                            text = "Change Theme",
                                                            fontSize = 14.sp
                                                        )
                                                    },
                                                    leadingIcon = {
                                                        Icon(
                                                            Icons.Default.Edit,
                                                            contentDescription = "Theme",
                                                            modifier = Modifier.size(22.dp)
                                                        )
                                                    },
                                                    onClick = {
                                                        showColorScreen = true
                                                        expanded = false
                                                    },
                                                )
                                            }
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

                                        }
                                    }
                                }
                            }
                            TimeBasedGreeting()
                            Text(
                                text = "Let's Keep Your Attendance on Point!",
                                color = MaterialTheme.colorScheme.secondary, // Lighter color
                                fontSize = 12.sp,
                                fontFamily = roundFontFamily,
                                fontWeight = FontWeight.Normal,
                                modifier = Modifier.alpha(0.5f)
                            )
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .heightIn(min = 500.dp)// Height based on content
                                    .padding(top = 25.dp, bottom = 30.dp)
                                    .clip(
                                        RoundedCornerShape(20.dp)
                                    )
                                    .background(contentColor.copy(alpha = 0.9f))

                                    .weight(1f)
                            ) {
                                val subjects by viewModel.subjects.collectAsState()
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
                                            onDelete = { viewModel.deleteSubject(subject) },
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
                                            viewModel2,
                                            backgroundColor
                                        )
                                    }
                                }
                                attendDetail?.let { subject ->
                                    AttendanceDialog(
                                        subject = subject,
                                        viewModel = viewModel2,
                                        onDismiss = { attendDetail = null },
                                        contentColor,
                                        backgroundColor
                                    )
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
                                                            backgroundColor,
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
                                                            backgroundColor
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
    }
}