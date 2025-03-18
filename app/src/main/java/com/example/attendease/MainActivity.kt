package com.example.attendease

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModelProvider

import androidx.lifecycle.lifecycleScope
import com.example.attendease.data.Subject
import com.example.attendease.data.SubjectDatabase
import com.example.attendease.data.SubjectRepository
import com.example.attendease.model.SubjectViewModel
import com.example.attendease.model.SubjectViewModelFactory
import com.example.attendease.screen.ChooseColorScreen
import com.example.attendease.screen.WelcomeScreen
import com.example.attendease.ui.theme.DarkColorScheme
import com.example.attendease.ui.theme.LightColorScheme

import com.example.attendease.ui.theme.coinyFontFamily
import com.example.attendease.ui.theme.nothingFontFamily
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlin.math.ceil


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userPreferences = UserPreferences(this)
        // Initialize Room Database
        val database = SubjectDatabase.getDatabase(applicationContext)

        // Initialize Repository
        val repository = SubjectRepository(database.subjectDao())

        // Initialize ViewModel using Factory
        val viewModelFactory = SubjectViewModelFactory(repository, application)
        val subjectViewModel = ViewModelProvider(this, viewModelFactory)[SubjectViewModel::class.java]


        lifecycleScope.launch {
            val name = userPreferences.getUserName.first() // Get stored name
            val storedColor = userPreferences.getThemeColor()
            if (name.isNullOrEmpty()) {
                // Show Welcome Screen if name is not set
                setContent {
                    WelcomeScreen(
                        onNavigateToMain = { recreate() } // Refresh to load Main UI
                    )
                }
            } else {
                // Show Main Screen with stored name
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && storedColor == null) {
                    setContent {
                        ChooseColorScreen()
                    }
                } else {
                    // Android 12+ uses Material You, Android < 12 uses selected color
                    setContent {
                        AppTheme(
                            dynamicColor = storedColor == null // Dynamic only if no stored color
                        ) {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && storedColor == null) {
                                ChooseColorScreen()
                            } else {
                                HomeScreen(name, storedColor,subjectViewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // ✅ Wallpaper-based theming on Android 12+
    selectedColor: Int? = null, // ✅ Selected color from ChooseColorScreen for Android 11 and below
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context) // ✅ Uses wallpaper-based theme colors
        }
        darkTheme -> DarkColorScheme
        selectedColor != null -> lightColorScheme( // ✅ Uses chosen color from ChooseColorScreen
            primary = Color(selectedColor)
        )
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}


@Composable
fun HomeScreen(userName: String, selectedColor: Int?,viewModel: SubjectViewModel) {
    var addSubjectDialog by rememberSaveable  { mutableStateOf(false) }
    var editSubjectDialog by rememberSaveable { mutableStateOf(false) }
    var selectedSubject by remember { mutableStateOf<Subject?>(null) }
    var text by remember { mutableStateOf("") }
    var total by remember { mutableStateOf("") }
    var attend by remember { mutableStateOf("") }
    val selectColor = selectedColor?.let { Color(it) } ?: MaterialTheme.colorScheme.primary
    val isAndroid12OrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val backgroundColor = if (isAndroid12OrAbove) {
        MaterialTheme.colorScheme.onPrimaryContainer
    } else {
        selectColor // Use the selected theme color from ChooseColorScreen
    }
    val contentColor = if (isAndroid12OrAbove) {
        MaterialTheme.colorScheme.primaryContainer
    } else {
        selectColor // Use the selected theme color from ChooseColorScreen
    }

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
                if(text.isNotEmpty() && attended != null && all != null){
                    viewModel.addSubject(name = text, attend = attended, total = all)
                    addSubjectDialog = false
                    resetFields()
                }
                addSubjectDialog = false
                // Handle the confirm action here
            }
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
                        editSubjectDialog = false
                        resetFields()
                    }
                }
            },
        )
    }



    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor.copy(alpha = 0.2f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 70.dp, start = 10.dp, end = 10.dp)
        ) {

            Column(
                modifier = Modifier
                    .padding(top = 10.dp)
                    .fillMaxSize()
            ) {
                Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                   ) {
                    Text(
                        text = "Hello",
                        color = contentColor,
                        fontSize = 32.sp,
                        fontFamily = nothingFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.alpha(0.6f)
                    )
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,){
                        Text(
                            text = userName,
                            color = MaterialTheme.colorScheme.tertiary,
                            fontSize = 32.sp,
                            fontFamily = nothingFontFamily,
                            fontWeight = FontWeight.ExtraBold,

                            )

                        IconButton(
                            onClick = { },
                            colors = IconButtonDefaults.iconButtonColors(contentColor.copy(alpha = 0.2f)),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.primary // Icon color
                            )
                        }
                    }
                }
                TimeBasedGreeting()
                Text(
                    text = "Let's Keep Your Attendance on Point!",
                    color = contentColor, // Lighter color
                    fontSize = 16.sp,
                    fontFamily = coinyFontFamily,
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
                        .background(contentColor.copy(alpha = 0.1f))
                        .weight(1f)
                ) {
                    val subjects by viewModel.subjects.collectAsState()
                    val listState = rememberLazyListState()

                    LazyColumn(
                        state = listState,
                        modifier = Modifier.padding(3.dp)) {
                        items(subjects, key = {it.id}) { subject ->
                            SubjectItem(subject, onPresent = { viewModel.markPresent(subject) },
                                onAbsent = { viewModel.markAbsent(subject) },
                                onDelete = {viewModel.deleteSubject(subject)},
                                onEdit = {
                                    selectedSubject = subject // Set the selected subject
                                    text = subject.name
                                    attend = subject.attend.toString()
                                    total = subject.total.toString()
                                    editSubjectDialog = true
                                })
                        }
                    }
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
                            .size(56.dp)
                            .align(Alignment.BottomEnd)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add Subject",
                            tint = if (isAndroid12OrAbove) {
                                MaterialTheme.colorScheme.tertiary
                            } else {
                                selectColor // Use the selected theme color from ChooseColorScreen
                            },
                            modifier = Modifier.size(56.dp)
                        )
                    }
                }

            }


        }

    }
}

@Composable
fun SubjectItem(subject: Subject, onPresent: () -> Unit, onAbsent: () -> Unit, onDelete: () -> Unit, onEdit: () -> Unit) {
    val backgroundColor = MaterialTheme.colorScheme.onPrimary
    val contentColor = MaterialTheme.colorScheme.tertiary
    var expanded by remember { mutableStateOf(false) }
    val requiredPercentage = 75
    val adviceText by remember(subject.attend, subject.total) {
        derivedStateOf {
            when {
                subject.attendancePercentage < requiredPercentage -> {
                    val neededClasses = ceil((0.75 * subject.total - subject.attend) / 0.25).toInt()
                    "You need to attend\n next $neededClasses classes"
                }

                subject.attendancePercentage > requiredPercentage -> {
                    val skipableClasses = ((subject.attend - 0.75 * subject.total) / 0.75).toInt()
                    when {
                        skipableClasses > 0 -> "You may leave\n $skipableClasses classes "
                        else -> "You can't miss\n your next class"
                    }
                }

                else -> "You can't miss\n" +
                        " your next class"
            }
        }
    }
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp),
        colors = CardColors(backgroundColor,contentColor,backgroundColor.copy(0.5f),contentColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
            Text(
                text = subject.name,
                fontFamily = nothingFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = 10.dp, start = 20.dp) // Ensures the Text is centered horizontally
            )
                Box {
                    IconButton(
                        onClick = { expanded = true },
                        colors = IconButtonDefaults.iconButtonColors(backgroundColor),
                        modifier = Modifier.size(46.dp)
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = null)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier
                            .background(backgroundColor.copy(alpha = 0.5f))
                    ) {
                        DropdownMenuItem(
                            text = { Text(text = "Edit Subject", fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = "Edit",
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            onClick = {
                                onEdit()
                                expanded = false
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(text = "Delete Subject", fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            onClick = {
                                onDelete()
                                expanded = false
                            },
                        )
                    }
                }
            }

                Column(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp)) {
                    Row {
                        Column {
                            Text(text = "Attended: ${subject.attend}", fontSize = 18.sp) // Left align
                            Text(text = "Total: ${subject.total}", modifier = Modifier.padding(top = 10.dp), fontSize = 18.sp)
                            Text(text = adviceText, modifier = Modifier.padding(top = 10.dp), fontSize = 14.sp)
                        }
                        Column {
                            Text(
                                text =  "Current percentage",
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(start=50.dp)
                            )
                            Text(
                                text = " ${subject.attendancePercentage}%",
                                fontFamily = nothingFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 38.sp,
                                modifier = Modifier.padding(start = 50.dp, top = 10.dp),
                                color = if (subject.attendancePercentage >= 75) MaterialTheme.colorScheme.tertiary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 20.dp)
                    ) {
                        Spacer(modifier = Modifier.weight(10f))
                        OutlinedIconButton(onClick = onPresent) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Attended"
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = onAbsent,
                            colors = IconButtonDefaults.iconButtonColors(
                                MaterialTheme.colorScheme.onPrimary.copy(
                                    alpha = 0.8f
                                )
                            ),
                            modifier = Modifier
                                .size(46.dp)
                            ) {
                            Icon(
                                painter = painterResource(id = R.drawable.close) ,
                                contentDescription = "Absent"
                            )
                        }


                    }



            }
        }
    }
}

