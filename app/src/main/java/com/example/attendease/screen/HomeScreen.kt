package com.example.attendease.screen

import android.os.Build
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendease.AddSubject
import com.example.attendease.EditSubject
import com.example.attendease.TimeBasedGreeting
import com.example.attendease.subjectdata.Subject
import com.example.attendease.model.SubjectViewModel
import com.example.attendease.ui.theme.coinyFontFamily
import com.example.attendease.ui.theme.nothingFontFamily
import com.example.attendease.uicomponent.SubjectItem

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
        MaterialTheme.colorScheme.onPrimary
    } else {
        selectColor // Use the selected theme color from ChooseColorScreen
    }

    val density = LocalDensity.current
    val gradientEndOffset = with(density) { 100.dp.toPx() }

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
                        Box{

                        IconButton(
                            onClick = { expanded=true },
                            colors = IconButtonDefaults.iconButtonColors(contentColor.copy(alpha = 0.2f)),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Settings,
                                contentDescription = "Settings",
                                tint = MaterialTheme.colorScheme.onTertiary // Icon color
                            )
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

                                    expanded = false
                                },
                            )
                            DropdownMenuItem(
                                text = { Text(text = "Reset", fontSize = 14.sp) },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Refresh,
                                        contentDescription = "Reset Attendance",
                                        modifier = Modifier.size(22.dp)
                                    )
                                },
                                onClick = {

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
                        .drawWithCache {
                            onDrawBehind {
                                // Draw Background Gradient
                                drawRect(
                                    Brush.linearGradient(
                                        colors =listOf(
                                            Color.White.copy(alpha = 1f),
                                            Color.White
                                        ),
                                        start = Offset(0f,size.height),
                                        end = Offset(0f,size.height-gradientEndOffset)
                                    )
                                )


                            }
                        }
                        .background(contentColor.copy(alpha = 0.5f))

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
                                },
                                onReset = {viewModel.resetAttendance(subject)}
                            )
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