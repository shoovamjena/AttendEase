package com.example.attendease.screen

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.lifecycleScope
import com.example.attendease.MainActivity
import com.example.attendease.UserPreferences
import kotlinx.coroutines.launch

@Composable
fun ChooseColorScreen() {
    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    val coroutineScope = rememberCoroutineScope()

    var selectedColor by remember { mutableStateOf(Color.Red) }
    val colorPickerState = remember { mutableStateOf(false) } // To toggle the color picker

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Choose Your Theme Color", fontSize = 24.sp, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(20.dp))

        Box(
            modifier = Modifier
                .size(100.dp)
                .background(selectedColor, CircleShape)
                .clickable { colorPickerState.value = true } // Open color picker on click
                .border(2.dp, Color.Black, CircleShape)
        )

        Spacer(modifier = Modifier.height(40.dp))

        Button(onClick = {
            coroutineScope.launch {
                userPreferences.saveThemeColor(selectedColor.toArgb())
                val intent = Intent(context, MainActivity::class.java)
                context.startActivity(intent)
            }
        }) {
            Text("CONFIRM")
        }

        // Show color picker dialog when clicked
        if (colorPickerState.value) {
            ColorPickerDialog(
                selectedColor = selectedColor,
                onColorSelected = { color ->
                    selectedColor = color
                    colorPickerState.value = false
                },
                onDismiss = { colorPickerState.value = false }
            )
        }
    }
}

/**
 * Custom Color Picker Dialog
 */
@Composable
fun ColorPickerDialog(
    selectedColor: Color,
    onColorSelected: (Color) -> Unit,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Pick a Color", fontSize = 20.sp, fontWeight = FontWeight.Bold)

                Spacer(modifier = Modifier.height(20.dp))

                var red by remember { mutableStateOf(selectedColor.red) }
                var green by remember { mutableStateOf(selectedColor.green) }
                var blue by remember { mutableStateOf(selectedColor.blue) }

                // Red Slider
                Text("Red: ${(red * 255).toInt()}")
                Slider(
                    value = red,
                    onValueChange = { red = it },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(thumbColor = Color.Red, activeTrackColor = Color.Red)
                )

                // Green Slider
                Text("Green: ${(green * 255).toInt()}")
                Slider(
                    value = green,
                    onValueChange = { green = it },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(thumbColor = Color.Green, activeTrackColor = Color.Green)
                )

                // Blue Slider
                Text("Blue: ${(blue * 255).toInt()}")
                Slider(
                    value = blue,
                    onValueChange = { blue = it },
                    valueRange = 0f..1f,
                    colors = SliderDefaults.colors(thumbColor = Color.Blue, activeTrackColor = Color.Blue)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Preview Color
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Color(red, green, blue), CircleShape)
                        .border(2.dp, Color.Black, CircleShape)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                    Button(onClick = onDismiss) { Text("Cancel") }
                    Button(onClick = { onColorSelected(Color(red, green, blue)) }) { Text("OK") }
                }
            }
        }
    }
}

