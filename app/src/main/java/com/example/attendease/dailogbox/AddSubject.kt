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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.attendease.ui.theme.nothingFontFamily


@Composable
fun AddSubject(
    subName: String,
    onSubNameChange: (String) -> Unit,
    classesAttended: String,
    onClassesAttendChange: (String) ->Unit,
    totalClasses: String,
    onTotalClassesChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    containerColor: Color,
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
                    color = containerColor,
                    shape = RoundedCornerShape(16.dp)
                )
                .padding(16.dp)
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "ADD SUBJECT",
                    fontFamily = nothingFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 26.sp,
                    color = MaterialTheme.colorScheme.primary
                )

                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    value = subName.take(20),
                    onValueChange = onSubNameChange,
                    label = {Text(text ="Add Subject") },
                    placeholder = {Text(text="Enter your Subject name", fontSize = 11.sp)},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    value = classesAttended,
                    onValueChange = onClassesAttendChange,
                    label = {Text(text ="Classes Attended") },
                    placeholder = {Text(text="Enter number of classes attended", fontSize = 11.sp)},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp),
                    value = totalClasses,
                    onValueChange = onTotalClassesChange,
                    label = {Text(text ="Total Classes") },
                    placeholder = {Text(text="Enter total number of classes", fontSize = 11.sp)},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = totalClasses<classesAttended
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    Button(onClick = onDismiss,
                        modifier = Modifier.shadow(if(isAndroid12OrAbove)5.dp else 0.dp, shape = RoundedCornerShape(50)),
                        colors = if (!isAndroid12OrAbove) ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary) else ButtonDefaults.buttonColors()) {
                        Text(text = "Cancel", color = MaterialTheme.colorScheme.onPrimary)
                    }
                    OutlinedButton(onClick = onConfirm) {
                        Text(text = "Confirm", color = MaterialTheme.colorScheme.primary)
                    }
                }
            }
        }
    }
}


