package com.example.attendease.dailogbox

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun ChangeTargetDialog(
    currentTarget: Float,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var sliderValue by remember { mutableFloatStateOf(currentTarget) }
    var editTextValue by remember { mutableStateOf(currentTarget.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Set Attendance Target") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                Text("Choose your attendance target (%)")

                Slider(
                    value = sliderValue,
                    onValueChange = {
                        sliderValue = it
                        editTextValue = it.toInt().toString()
                    },
                    valueRange = 50f..100f,
                    steps = 50,
                )

                OutlinedTextField(
                    value = editTextValue,
                    onValueChange = { text ->
                        editTextValue = text
                        text.toFloatOrNull()?.let {
                            if (it in 50f..100f) {
                                sliderValue = it
                            }
                        }
                    },
                    label = { Text("Target (%)") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
                )

            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm(sliderValue.toInt())
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error.copy(alpha = 0.65f))){
                Text("Cancel")
            }
        }
    )
}
