package com.example.attendease.dailogbox

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.attendease.ui.theme.roundFontFamily

@Composable
fun EditAttendanceDialog(
    onDismiss: () -> Unit,
    containerColor: Color,
    onPresent: () -> Unit,
    onAbsent: () -> Unit,
    status: String,
    isAndroid12OrAbove: Boolean
){



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
                        text = "CHANGE STATUS",
                        fontFamily = nothingFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "Current Status : $status",
                        fontFamily = roundFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        OutlinedButton(
                            onClick = onPresent
                        ) {
                            Text(text = "PRESENT", color = MaterialTheme.colorScheme.primary, fontFamily = nothingFontFamily)
                        }
                        Button(
                            onClick = onAbsent,
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error.copy(0.8f)),
                            modifier = Modifier.shadow(if(isAndroid12OrAbove)5.dp else 0.dp, shape = RoundedCornerShape(50))
                        ) {
                            Text(text = "ABSENT", color = MaterialTheme.colorScheme.onPrimary, fontFamily = nothingFontFamily)
                        }
                    }

            }
        }
    }
}