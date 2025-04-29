package com.example.attendease.dailogbox

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.attendease.model.SubjectViewModel
import com.example.attendease.ui.theme.nothingFontFamily

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
    viewModel: SubjectViewModel
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

                // Subject Dropdown
                Box {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        value = subName,
                        onValueChange = onSubNameChange,
                        label = { Text(text = "Edit Subject") },
                        placeholder = { Text(text = "Choose Subject", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expandedSubject = !expandedSubject }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expandedSubject,
                        onDismissRequest = { expandedSubject = false },
                        modifier = Modifier.background(containerColor.copy(0.4f)),
                    ) {
                        subject.forEach { subject ->
                            DropdownMenuItem(
                                text = { Text(subject.name) },
                                onClick = {
                                    onSubNameChange(subject.name)
                                    expandedSubject = false
                                }
                            )
                        }
                    }
                }

                // Day Dropdown
                Box {
                    OutlinedTextField(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 10.dp),
                        value = day,
                        onValueChange = onDayChanged,
                        label = { Text(text = "Select Day") },
                        placeholder = { Text(text = "Choose Day", fontSize = 11.sp) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        readOnly = true,
                        trailingIcon = {
                            IconButton(onClick = { expandedDay = !expandedDay }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        }
                    )
                    DropdownMenu(
                        expanded = expandedDay,
                        onDismissRequest = { expandedDay = false },
                        modifier = Modifier.background(containerColor.copy(0.4f))
                    ) {
                        days.forEach { d ->
                            DropdownMenuItem(
                                text = { Text(d) },
                                onClick = {
                                    onDayChanged(d)
                                    expandedDay = false
                                }
                            )
                        }
                    }
                }

                // Start Time
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    value = startTime,
                    onValueChange = onStartTimeChanged,
                    label = { Text(text = "Select Start Time") },
                    placeholder = { Text(text = "Choose Start Time", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { startPicker = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                )
                if (startPicker) {
                    Dialog(onDismissRequest = { startPicker = false }) {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
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
                                        val isPm = selectedHour>=12
                                        val hourIn12Form = if(selectedHour%12==0)12 else selectedHour%12
                                        val amPm =if(isPm)"PM" else "AM"
                                        val selectedMinute = startTimePickerState.minute
                                        val formattedTime = String.format("%02d:%02d %s", hourIn12Form, selectedMinute, amPm)
                                        onStartTimeChanged(formattedTime)
                                        startPicker = false }) {
                                        Text("OK")
                                    }
                                }
                            }
                        }
                    }
                }

                // End Time
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    value = endTime,
                    onValueChange = onEndTimeChanged,
                    label = { Text(text = "Select End Time") },
                    placeholder = { Text(text = "Choose End Time", fontSize = 11.sp) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { endPicker = true }) {
                            Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                        }
                    }
                )
                if (endPicker) {
                    Dialog(onDismissRequest = { endPicker = false }) {
                        Box(
                            modifier = Modifier
                                .background(MaterialTheme.colorScheme.surface, shape = RoundedCornerShape(16.dp))
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
                                        val isPm = selectedHour>=12
                                        val hourIn12Form = if(selectedHour%12==0)12 else selectedHour%12
                                        val amPm =if(isPm)"PM" else "AM"
                                        val selectedMinute = endTimePickerState.minute
                                        val formattedTime = String.format("%02d:%02d %s", hourIn12Form, selectedMinute, amPm)
                                        onEndTimeChanged(formattedTime)
                                        endPicker = false }) {
                                        Text("OK")
                                    }
                                }
                            }
                        }
                    }
                }

                // Action Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = onDismiss,
                        modifier = Modifier.shadow(5.dp, shape = RoundedCornerShape(50))) {
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
