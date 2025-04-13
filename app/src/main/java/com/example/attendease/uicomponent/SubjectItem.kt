package com.example.attendease.uicomponent

import android.os.Build
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendease.R
import com.example.attendease.UserPreferences
import com.example.attendease.model.DetailViewModel
import com.example.attendease.subjectdata.Subject
import com.example.attendease.ui.theme.nothingFontFamily
import kotlin.math.ceil

@Composable
fun SubjectItem(subject: Subject, onPresent: () -> Unit, onAbsent: () -> Unit, onDelete: () -> Unit, onEdit: () -> Unit, onReset:() -> Unit, onClick:() -> Unit, viewModel: DetailViewModel) {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val isLava = Build.BRAND.equals("lava", ignoreCase = true)
    val backgroundColor = if(isLava){ MaterialTheme.colorScheme.onSecondary }else{MaterialTheme.colorScheme.onTertiary}
    val contentColor = MaterialTheme.colorScheme.secondary
    var expanded by remember { mutableStateOf(false) }
    val currentTarget by userPreferences.targetFlow.collectAsState(initial = 75f)
    val requiredPercentage = currentTarget
    val targetDecimal = requiredPercentage/100f
    val adviceText =
        when {
            (subject.attendancePercentage < requiredPercentage && subject.attendancePercentage!=0) -> {
                val neededClasses = ceil((targetDecimal * subject.total - subject.attend) / (1-targetDecimal)).toInt()
                "Attend next $neededClasses\nclasses to get on track"
            }

            subject.attendancePercentage > requiredPercentage -> {
                val skippableClasses = ((subject.attend - targetDecimal * subject.total) / targetDecimal).toInt()
                when {
                    skippableClasses > 0 -> "You may leave\n$skippableClasses classes "
                    else -> "You can't miss\nyour next class"
                }
            }

            subject.attendancePercentage == 0 -> {
                "New Session Begins!!"
            }

            else -> "You can't miss\n" +
                    "your next class"
        }

    val animatedBarPercentage by animateFloatAsState(
        targetValue = subject.attendancePercentage.toFloat(),
        animationSpec = tween(durationMillis = 1000),
        label = "AttendanceAnimation"
    )


    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 5.dp),
        colors = CardColors(backgroundColor,contentColor.copy(alpha = 0.7f),backgroundColor,contentColor.copy(alpha = 0.5f))
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .clickable(onClick = onClick)
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
                        colors = IconButtonDefaults.iconButtonColors(Color.Transparent),
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
                                onReset()
                                expanded = false
                            },
                        )
                        DropdownMenuItem(
                            text = { Text(text = "View Details", fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Info,
                                    contentDescription = "Attendance Details",
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            onClick = {
                                onClick()
                                expanded = false
                            },
                        )
                    }
                }
            }

            Column(modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
                ,) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column {
                        Text(text = "Attended: ${subject.attend}", fontSize = 18.sp) // Left align
                        Text(text = "Total: ${subject.total}", modifier = Modifier.padding(top = 10.dp), fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Column {
                        val animatedPercentage by animateIntAsState(
                            targetValue = subject.attendancePercentage,
                            animationSpec = tween(durationMillis = 500, easing = FastOutLinearInEasing)
                        )
                        AnimatedAttendanceProgressBar(animatedBarPercentage,requiredPercentage)
                        Text(
                            text = " ${animatedPercentage}%",
                            fontFamily = nothingFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 38.sp,
                            modifier = Modifier.padding(start = 50.dp, top = 10.dp),
                            color = if (subject.attendancePercentage >= requiredPercentage) MaterialTheme.colorScheme.secondary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                        )
                    }
                }
                Column {
                    Row (
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(end = 20.dp)

                    ) {
                        Text(text = adviceText, fontSize = 14.sp)
                        Spacer(modifier = Modifier.weight(10f))
                        OutlinedIconButton(
                            onClick = {
                                onPresent()
                                viewModel.insertAttendanceRecord(subject.id, "Present")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Done,
                                contentDescription = "Attended"
                            )
                        }
                        Spacer(modifier = Modifier.weight(1f))
                        IconButton(
                            onClick = {
                                onAbsent()
                                viewModel.insertAttendanceRecord(subject.id, "Absent")
                            },
                            colors = IconButtonDefaults.iconButtonColors(
                                Color.Transparent
                            ),
                            modifier = Modifier
                                .size(46.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.close),
                                contentDescription = "Absent"
                            )
                        }
                    }
                }

            }
        }
    }
}


