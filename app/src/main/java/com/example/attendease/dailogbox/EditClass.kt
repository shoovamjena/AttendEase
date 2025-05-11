package com.example.attendease.dailogbox

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.attendease.ui.theme.nothingFontFamily
import com.example.attendease.viewmodel.SubjectViewModel

@SuppressLint("DefaultLocale")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditClass(
    subName: String,
    onSubNameChange: (String) -> Unit,
    day: String,
    onDayChanged: (String) -> Unit,
    startTime: String,
    onStartTimeChanged: (String) -> Unit,
    endTime: String,
    onEndTimeChanged: (String) -> Unit,
    onDismiss: () -> Unit,
    onUpdate: () -> Unit,
    containerColor: Color,
    viewModel: SubjectViewModel,
    isAndroid12OrAbove: Boolean
) {
    val startTimePickerState = rememberTimePickerState()
    var startPicker by remember { mutableStateOf(false) }
    val endTimePickerState = rememberTimePickerState()
    var endPicker by remember { mutableStateOf(false) }
    val subject by viewModel.subjects.collectAsState()
    var expandedSubject by remember { mutableStateOf(false) }
    var expandedDay by remember { mutableStateOf(false) }
    val days = listOf("Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday")

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            dismissOnClickOutside = true,
            dismissOnBackPress = true
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .background(
                    color = containerColor.copy(alpha = 1f),
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "EDIT CLASS",
                    fontFamily = nothingFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expandedSubject = !expandedSubject },
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        colors = CardDefaults.cardColors(containerColor)
                    ) {
                        Text(
                            text = "Add Subject",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth(),verticalArrangement = Arrangement.Center) {

                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = subName.ifEmpty { "Choose Subject" },
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (subName.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    DropdownMenu(
                        expanded = expandedSubject,
                        onDismissRequest = { expandedSubject = false },
                        modifier = Modifier.background(containerColor.copy(0.4f))
                    ) {
                        subject.forEach { subjectItem ->
                            DropdownMenuItem(
                                text = { Text(subjectItem.name) },
                                onClick = {
                                    onSubNameChange(subjectItem.name)
                                    expandedSubject = false
                                }
                            )
                        }
                    }
                }
                Box {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp)
                            .clickable { expandedDay = !expandedDay },
                        shape = RoundedCornerShape(8.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                        colors = CardDefaults.cardColors(containerColor)
                    ) {
                        Text(
                            text = "Select Day",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(start = 10.dp)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(60.dp)
                                .border(
                                    width = 2.dp,
                                    color = MaterialTheme.colorScheme.primary,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Column(modifier = Modifier.fillMaxWidth(),verticalArrangement = Arrangement.Center) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = day.ifEmpty { "Choose Day" },
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = if (day.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                        else MaterialTheme.colorScheme.onSurface
                                    )
                                    Icon(
                                        imageVector = Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    DropdownMenu(
                        expanded = expandedDay,
                        onDismissRequest = { expandedDay = false },
                        modifier = Modifier.background(containerColor.copy(0.4f))
                    ) {
                        days.forEach { dayItem ->
                            DropdownMenuItem(
                                text = { Text(dayItem) },
                                onClick = {
                                    onDayChanged(dayItem)
                                    expandedDay = false
                                }
                            )
                        }
                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(containerColor)
                ) {
                    Text(
                        text = "Select Start Time",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clickable { startPicker = true }
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = startTime.ifEmpty { "Choose Start Time" },
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (startTime.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    else MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
                if (startPicker) {
                    Dialog(onDismissRequest = { startPicker = false }) {
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                TimePicker(state = startTimePickerState)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(onClick = { startPicker = false }) {
                                        Text("CANCEL")
                                    }
                                    OutlinedButton(onClick = {
                                        val selectedHour = startTimePickerState.hour
                                        val isPm = selectedHour >= 12
                                        val hourIn12Form =
                                            if (selectedHour % 12 == 0) 12 else selectedHour % 12
                                        val amPm = if (isPm) "PM" else "AM"
                                        val selectedMinute = startTimePickerState.minute
                                        val formattedTime = String.format(
                                            "%02d:%02d %s",
                                            hourIn12Form,
                                            selectedMinute,
                                            amPm
                                        )
                                        onStartTimeChanged(formattedTime)
                                        startPicker = false
                                    }) {
                                        Text("OK")
                                    }
                                }
                            }
                        }
                    }
                }
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    shape = RoundedCornerShape(8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    colors = CardDefaults.cardColors(containerColor)
                ) {
                    Text(
                        text = "Select End Time",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(start = 10.dp)
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .clickable { endPicker = true }
                            .border(
                                width = 2.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(12.dp)
                            )
                            .padding(10.dp)
                    ) {
                        Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.Center) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = endTime.ifEmpty { "Choose End Time" },
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = if (endTime.isEmpty()) MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                                    else MaterialTheme.colorScheme.onSurface
                                )
                                Icon(
                                    imageVector = Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }

                if (endPicker) {
                    Dialog(onDismissRequest = { endPicker = false }) {
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.surface,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .padding(16.dp)
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                TimePicker(state = endTimePickerState)
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    Button(onClick = { endPicker = false }) {
                                        Text("CANCEL")
                                    }
                                    OutlinedButton(onClick = {
                                        val selectedHour = endTimePickerState.hour
                                        val isPm = selectedHour >= 12
                                        val hourIn12Form =
                                            if (selectedHour % 12 == 0) 12 else selectedHour % 12
                                        val amPm = if (isPm) "PM" else "AM"
                                        val selectedMinute = endTimePickerState.minute
                                        val formattedTime = String.format(
                                            "%02d:%02d %s",
                                            hourIn12Form,
                                            selectedMinute,
                                            amPm
                                        )
                                        onEndTimeChanged(formattedTime)
                                        endPicker = false
                                    }) {
                                        Text("OK")
                                    }
                                }
                            }
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = onDismiss,
                        modifier = Modifier.shadow(if(isAndroid12OrAbove)5.dp else 0.dp, shape = RoundedCornerShape(50))) {
                        Text(text = "CANCEL", color = MaterialTheme.colorScheme.onPrimary, fontFamily = nothingFontFamily)
                    }
                    OutlinedButton(onClick = onUpdate) {
                        Text(text = "UPDATE", color = MaterialTheme.colorScheme.primary, fontFamily = nothingFontFamily)
                    }
                }
            }
        }
    }
}
