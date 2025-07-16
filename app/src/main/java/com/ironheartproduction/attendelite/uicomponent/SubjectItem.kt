package com.ironheartproduction.attendelite.uicomponent

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironheartproduction.attendelite.R
import com.ironheartproduction.attendelite.UserPreferences
import com.ironheartproduction.attendelite.dailogbox.DeleteDialog
import com.ironheartproduction.attendelite.dailogbox.ResetDialog
import com.ironheartproduction.attendelite.viewmodel.DetailViewModel
import com.ironheartproduction.attendelite.model.subjectdata.Subject
import com.ironheartproduction.attendelite.ui.theme.nothingFontFamily
import kotlin.math.ceil

@Composable
fun SubjectItem(
    subject: Subject,
    onPresent: () -> Unit,
    onAbsent: () -> Unit,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onClick: () -> Unit,
    onReset: () -> Unit,
    viewModel: DetailViewModel,
    backgroundColor: Color,
    dialogColor: Color
) {
    val isAndroid12OrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    Build.BRAND.equals("lava", ignoreCase = true)
    val contentColor = MaterialTheme.colorScheme.primary
    var expanded by remember { mutableStateOf(false) }
    var deleteDialog by remember { mutableStateOf(false) }
    var resetDialog by remember { mutableStateOf(false) }
    val currentTarget by userPreferences.targetFlow.collectAsState(initial = 75f)
    val requiredPercentage = currentTarget
    val targetDecimal = requiredPercentage/100f
    val adviceText =
        when {
            (subject.attendancePercentage < requiredPercentage && subject.total!=0) -> {
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

            subject.attendancePercentage == 0 && subject.total == 0 -> {
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
    
    if (deleteDialog){
        DeleteDialog(
            onConfirm = {
                onDelete()
            },
            onDismiss = {
                deleteDialog = false
                expanded = false},
            containerColor = dialogColor,
            text = subject.name,
            toast = "${subject.name} IS DELETED!!",
            isAndroid12OrAbove = isAndroid12OrAbove
        )
    }
    if (resetDialog){
        ResetDialog(
            onConfirm = {
                onReset()
            },
            onDismiss = {
                resetDialog = false
                expanded = false},
            containerColor = dialogColor,
            toast = "${subject.name} is Reset",
            isAndroid12OrAbove = isAndroid12OrAbove
        )
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp),
        colors = CardColors(backgroundColor,contentColor,backgroundColor,contentColor),
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AdaptiveText(subject.name,32,4,10,modifier = Modifier.padding(top = 10.dp, start = 20.dp),
                    nothingFontFamily)
                Spacer(modifier = Modifier.weight(1f))
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
                                deleteDialog = true
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
                                resetDialog = true
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

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Column {
                        Text(text = "Attended: ${subject.attend}", fontSize = 18.sp) // Left align
                        Text(text = "Total: ${subject.total}", modifier = Modifier.padding(top = 10.dp), fontSize = 18.sp)
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    Column {
                        val animatedPercentage by animateIntAsState(
                            targetValue = subject.attendancePercentage,
                            animationSpec = tween(durationMillis = 500, easing = FastOutLinearInEasing),
                            label = ""
                        )
                        AnimatedAttendanceProgressBar(animatedBarPercentage,requiredPercentage)
                        Text(
                            text = " ${animatedPercentage}%",
                            fontFamily = nothingFontFamily,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 38.sp,
                            modifier = Modifier.padding(start = 50.dp, top = 10.dp),
                            color = if (subject.attendancePercentage >= requiredPercentage) MaterialTheme.colorScheme.primary.copy(alpha = 0.7f) else MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
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
                                .shadow(
                                    elevation = 5.dp,
                                    shape = RoundedCornerShape(50),
                                )
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


