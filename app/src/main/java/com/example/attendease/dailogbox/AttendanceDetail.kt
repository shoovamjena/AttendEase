package com.example.attendease.dailogbox

import android.os.Build
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
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.example.attendease.model.DetailViewModel
import com.example.attendease.subjectdata.Subject
import com.example.attendease.ui.theme.nothingFontFamily
import com.example.attendease.uicomponent.AdaptiveText
import com.example.attendease.uicomponent.AttendanceItem

@Composable
fun AttendanceDialog(subject: Subject, viewModel: DetailViewModel, onDismiss: () -> Unit,backgroundColor: Color,containerColor: Color,navController: NavController, isAndroid12OrAbove: Boolean) {
    // Holds attendance records from ViewModel
    val attendanceRecords by viewModel.attendanceRecords.collectAsState()
    Build.BRAND.equals("lava", ignoreCase = true)

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
                .background(backgroundColor)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                //Custom Function to Handle Text Size
                AdaptiveText(subject.name,38,6,10,modifier = Modifier.padding(top = 10.dp, start = 20.dp),
                    nothingFontFamily)
                Column (
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.SpaceBetween) {
                Text(
                    text = "Attendance : ${subject.attend}/${subject.total}",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(top = 10.dp)
                )
                Text(
                    text = "Attended : ${subject.attendancePercentage} %",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Light,
                    modifier = Modifier.padding(top = 10.dp)
                )
                }

                if (attendanceRecords.isEmpty()) {
                    Text(
                        text = "No attendance records found",
                        fontSize = 14.sp,
                        color = Color.Black,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(top = 10.dp),
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
                            .background(containerColor.copy(0.7f))// Keeps LazyColumn scrollable
                    ) {

                        LazyColumn {
                            items(attendanceRecords) { record ->
                                AttendanceItem(record)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                if(attendanceRecords.isEmpty()){
                    Button(
                        onClick = onDismiss,
                        modifier = Modifier.align(Alignment.CenterHorizontally).shadow(5.dp, shape = RoundedCornerShape(50))
                    ) {
                        Text("Close")
                    }
                }else{
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ){
                        Button(
                            onClick = onDismiss,
                            modifier = Modifier.shadow(if(isAndroid12OrAbove)5.dp else 0.dp, shape = RoundedCornerShape(50))
                        ) {
                            Text(" CLOSE",
                                fontFamily = nothingFontFamily)
                        }
                        OutlinedButton (
                            onClick = {
                                onDismiss()
                                navController.navigate("attendanceDetail/${subject.name}")
                            },
                        ) {
                            Text(" EDIT",
                                fontFamily = nothingFontFamily)
                        }
                    }
                }
            }
        }
    }
}
