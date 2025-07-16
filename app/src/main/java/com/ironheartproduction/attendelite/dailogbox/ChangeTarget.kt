package com.ironheartproduction.attendelite.dailogbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ironheartproduction.attendelite.ui.theme.nothingFontFamily

@Composable
fun ChangeTargetDialog(
    currentTarget: Float,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
    contentColor: Color,
    isAndroid12OrAbove: Boolean
) {
    var sliderValue by remember { mutableFloatStateOf(currentTarget) }
    var editTextValue by remember { mutableStateOf(currentTarget.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = contentColor,
        title = { Text("Set Attendance Target",fontFamily = nothingFontFamily) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Choose your attendance target (%)",fontFamily = nothingFontFamily)

                Slider(
                    value = sliderValue,
                    onValueChange = {
                        sliderValue = it
                        editTextValue = it.toInt().toString()
                    },
                    valueRange = 20f..90f,
                    steps = 70,
                )

                OutlinedTextField(
                    value = editTextValue,
                    onValueChange = { text ->
                        editTextValue = text
                        text.toFloatOrNull()?.let {
                            if (it in 20f..90f) {
                                sliderValue = it
                            }
                        }
                    },
                    label = { Text("Target (%)",fontFamily = nothingFontFamily) },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

            }
        },
        confirmButton = {
            OutlinedButton(
                onClick = {
                    onConfirm(sliderValue.toInt())
                }
            ) {
                Text("SAVE",fontFamily = nothingFontFamily,textAlign = TextAlign.Center)
            }
        },
        dismissButton = {
            Button(onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                modifier = Modifier.shadow(if(isAndroid12OrAbove)5.dp else 0.dp, shape = RoundedCornerShape(50))){
                Text("CANCEL",fontFamily = nothingFontFamily, textAlign = TextAlign.Center)
            }
        }
    )
}
