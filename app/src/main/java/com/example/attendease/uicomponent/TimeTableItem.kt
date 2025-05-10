package com.example.attendease.uicomponent

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendease.dailogbox.DeleteDialog
import com.example.attendease.timetabledata.Timetable
import com.example.attendease.ui.theme.nothingFontFamily

@Composable
fun TimeTableItem(
    timetable: Timetable,
    onDelete: () -> Unit,
    onEdit: () -> Unit,
    onClick: () -> Unit,
    backgroundColor: Color,
    dialogColor: Color,
    isAndroid12OrAbove: Boolean
) {
    val contentColor = MaterialTheme.colorScheme.primary
    var expanded by remember { mutableStateOf(false) }
    var deleteDialog by remember { mutableStateOf(false) }

    if (deleteDialog){
        DeleteDialog(
            onConfirm = {
                onDelete()
            },
            onDismiss = {
                deleteDialog = false
                expanded = false},
            containerColor = dialogColor,
            text = timetable.subjectName,
            toast = "${timetable.subjectName} CLASS AT ${timetable.startTime} IS DELETED",
            isAndroid12OrAbove = isAndroid12OrAbove
        )
    }


    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .shadow(15.dp),
        colors = CardColors(backgroundColor,contentColor.copy(alpha = 0.7f),backgroundColor,contentColor.copy(alpha = 0.5f)),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                modifier = Modifier.fillMaxSize().padding(start = 45.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${timetable.startTime} - ${timetable.endTime}",
                    fontFamily = nothingFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(start = 10.dp)

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
                            text = { Text(text = "Edit Class", fontSize = 14.sp) },
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
                            text = { Text(text = "Delete Class", fontSize = 14.sp) },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.Delete,
                                    contentDescription = "Delete",
                                    modifier = Modifier.size(22.dp)
                                )
                            },
                            onClick = {
                                deleteDialog = true
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
            AdaptiveText(
                timetable.subjectName,38,3,10,modifier = Modifier,
                nothingFontFamily)
        }
    }
}