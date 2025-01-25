package com.example.attendease

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendease.model.SubjectViewModel
import com.example.attendease.ui.theme.AttendEaseTheme
import com.example.attendease.ui.theme.coinyFontFamily
import com.example.attendease.ui.theme.nothingFontFamily
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    val subjectViewModel by viewModel<SubjectViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AttendEaseTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HomeScreen(viewModel = subjectViewModel)
                }
            }
        }
    }
}


@Composable
fun HomeScreen(viewModel: SubjectViewModel) {
    val backgroundColor = MaterialTheme.colorScheme.onPrimary
    val contentColor = MaterialTheme.colorScheme.primary
    var addSubjectDialog by rememberSaveable  { mutableStateOf(false) }
    var text by remember { mutableStateOf("") }
    var total by remember { mutableStateOf("") }
    var attend by remember { mutableStateOf("") }


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
            clasesAtended = attend,
            onClassesAttendChange = { attend = it },
            onDismiss = {
                resetFields()
                addSubjectDialog = false },
            onConfirm = {
                val attended = attend.toIntOrNull()
                val all = total.toIntOrNull()
                if(text.isNotEmpty() && attended != null && all != null){
                    viewModel.addSubject(text, attended, all)
                    addSubjectDialog = false
                    resetFields()
                }
                addSubjectDialog = false
                // Handle the confirm action here
            }
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
                Row(modifier = Modifier.padding(bottom = 10.dp)) {
                    Text(
                        text = "Hello",
                        color = contentColor,
                        fontSize = 32.sp,
                        fontFamily = nothingFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.alpha(0.6f)
                    )

                    Text(
                        text = "SHOOVAM",
                        color = contentColor,
                        fontSize = 32.sp,
                        fontFamily = nothingFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.padding(start = 15.dp)
                    )
                    Spacer(modifier = Modifier.width(30.dp)) //acts like <br> tag of html
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
                TimeBasedGreeting()
                Text(
                    text = "Let's Keep Your Attendance on Point!",
                    color = contentColor.copy(alpha = 0.7f), // Lighter color
                    fontSize = 16.sp,
                    fontFamily = coinyFontFamily,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.alpha(0.5f)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 585.dp)// Height based on content
                        .padding(top = 25.dp, bottom = 30.dp)
                        .clip(
                            RoundedCornerShape(
                                bottomStart = 40.dp,
                                bottomEnd = 40.dp,
                                topStart = 20.dp,
                                topEnd = 20.dp
                            )
                        )
                        .background(contentColor.copy(alpha = 0.1f))
                        .weight(1f)
                ) {
                    val subjects by viewModel.subjects.collectAsState()
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(subjects) { subject ->
                            SubjectItem(subjectName = subject.subjectName, attended = subject.attendedClasses, total = subject.totalClasses,
                                onAttendedChanged = { newAttended, newTotal ->
                                    viewModel.updateAttendance(subject.subjectName, newAttended, newTotal)
                                },
                                onAbsentChanged = { newAttended, newTotal ->
                                    viewModel.updateAttendance(subject.subjectName, newAttended, newTotal)
                                }
                                )
                        }
                    }
                }

            }


        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp)
                .padding(start = 10.dp, end = 10.dp, bottom = 30.dp)
                .align(Alignment.BottomCenter)
                .shadow(
                    elevation = 20.dp, // Adjust the elevation for the shadow intensity
                    shape = RoundedCornerShape(50.dp), // Match the shape of the Box
                    clip = false // Ensure the shadow is outside the bounds of the Box
                )
                .clip(RoundedCornerShape(50.dp))
                .background(contentColor.copy(alpha = 0.6f))
        ) {
            IconButton(
                onClick = { addSubjectDialog = true },
                colors = IconButtonDefaults.iconButtonColors(
                    MaterialTheme.colorScheme.onPrimary.copy(
                        alpha = 0.8f
                    )
                ),
                modifier = Modifier
                    .size(46.dp)
                    .align(Alignment.Center)

            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Subject",
                    tint = contentColor.copy(alpha = 0.7f), // Icon color
                    modifier = Modifier.size(56.dp)
                )
            }
        }
    }
}

@Composable
fun SubjectItem(subjectName: String, attended: Int, total: Int,onAttendedChanged: (Int, Int) -> Unit, // We pass both attended and total
                onAbsentChanged: (Int, Int) -> Unit) {
    val backgroundColor = MaterialTheme.colorScheme.onPrimary
    val contentColor = MaterialTheme.colorScheme.primary
    val attendancePercentage = if (total != 0)
    {(attended.toFloat() / total * 100).toInt() } else 0
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 10.dp),
        colors = CardColors(backgroundColor,contentColor,backgroundColor.copy(0.5f),contentColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = "$subjectName",
                fontFamily = nothingFontFamily,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth() // Ensures the Text is centered horizontally
            )

                Column(modifier = Modifier.fillMaxWidth().padding(top = 20.dp)) {
                    Row {
                        Column {
                            Text(text = "Attended: $attended", fontSize = 18.sp) // Left align
                            Text(text = "Total: $total", modifier = Modifier.padding(top = 10.dp), fontSize = 18.sp)
                        }
                        Column {
                            Text(
                                text =  "Current percentage",
                                fontSize = 14.sp,
                                modifier = Modifier
                                    .padding(start=50.dp)
                            )
                            Text(
                                text = " $attendancePercentage %",
                                fontFamily = nothingFontFamily,
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 48.sp,
                                modifier = Modifier.padding(start = 50.dp, top = 10.dp),
                                color = if (attendancePercentage >= 75) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 20.dp)
                    ) {
                        Spacer(modifier = Modifier.weight(10f))
                        OutlinedIconButton(onClick = {
                            onAttendedChanged(attended + 1, total + 1)
                        }) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Attended"
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = {
                                onAbsentChanged(attended,total+1)
                            },
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

