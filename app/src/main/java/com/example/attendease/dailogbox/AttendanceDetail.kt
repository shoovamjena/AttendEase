package com.example.attendease.dailogbox

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.attendease.model.DetailViewModel
import com.example.attendease.subjectdata.Subject
import com.example.attendease.ui.theme.nothingFontFamily
import com.example.attendease.uicomponent.AttendanceItem

@Composable
fun AttendanceDialog(subject: Subject, viewModel: DetailViewModel, onDismiss: () -> Unit) {
    // Holds attendance records from ViewModel
    val attendanceRecords by viewModel.attendanceRecords.collectAsState()

    // Load attendance when dialog opens
    LaunchedEffect(subject.id) {
        viewModel.getAttendanceRecords(subject.id)
    }

    Dialog(onDismissRequest = onDismiss) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(20.dp))
                .background(MaterialTheme.colorScheme.onPrimary)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .align(Alignment.Center),

            ) {
                Text(
                    text = subject.name,
                    fontSize = 38.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.align(Alignment.CenterHorizontally),
                    fontFamily = nothingFontFamily
                )
                Column (
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Attendance : ${subject.attend}/${subject.total}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraLight,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    text = "Attended : ${subject.attendancePercentage} %",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraLight,
                    modifier = Modifier.padding(top = 10.dp)
                )
                }

                if (attendanceRecords.isEmpty()) {
                    Text(
                        text = "No attendance records found",
                        fontSize = 14.sp,
                        color = Color.Gray,
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 10.dp),
                        fontFamily = nothingFontFamily
                    )
                } else {
                    Spacer(modifier = Modifier.height(15.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "Date and Time", fontSize = 18.sp, fontWeight = FontWeight.Medium)
                        Text(text = "Status", fontSize = 18.sp, fontWeight = FontWeight.Bold,
                        )
                    }

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f))// Keeps LazyColumn scrollable
                    ) {

                        LazyColumn {
                            items(attendanceRecords) { record ->
                                AttendanceItem(record)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text("Close")
                }
            }
        }
    }
}
