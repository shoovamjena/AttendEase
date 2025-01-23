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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendease.ui.theme.AttendEaseTheme
import com.example.attendease.ui.theme.coinyFontFamily

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AttendEaseTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Greeting(
                        name = "Android",
                        modifier = Modifier.padding(innerPadding)
                    )
                    HomeScreen()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    AttendEaseTheme {
        Greeting("Android")
    }
}

@Composable
fun HomeScreen() {
    val backgroundColor = MaterialTheme.colorScheme.primaryContainer
    val contentColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor) // Set background color
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top=70.dp, start = 20.dp)
        ) {
            Column(modifier = Modifier.padding(top = 10.dp)) {
                Row(modifier = Modifier.padding(bottom = 10.dp)) {
                    Text(
                        text = "Hello",
                        color = contentColor,
                        fontSize = 32.sp,
                        fontFamily = coinyFontFamily,
                        fontWeight = FontWeight.ExtraBold,
                        modifier = Modifier.alpha(0.7f)
                    )
                    // @name part with larger and bolder text
                    Text(
                        text = "@name",
                        color = contentColor,
                        fontSize = 32.sp, // Larger font size
                        fontFamily = coinyFontFamily,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(start = 15.dp)// Bolder
                    )
                }
                Text(
                    text = "GOOD MORNING!!",
                    color = contentColor,
                    fontSize = 32.sp,
                    fontFamily = coinyFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.alpha(0.7f)
                        .padding(top = 10.dp)
                )

                // Let's with smaller and lighter text
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Let's Keep Your Attendance on Point!",
                    color = contentColor.copy(alpha = 0.5f), // Lighter color
                    fontSize = 18.sp, // Smaller font size
                    fontFamily = coinyFontFamily,
                    fontWeight = FontWeight.Normal, // Lighter weight
                    modifier = Modifier.alpha(0.7f)
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(start = 10.dp, end = 10.dp, bottom = 30.dp)
                .align(Alignment.BottomCenter)
                .shadow(
                    elevation = 20.dp, // Adjust the elevation for the shadow intensity
                    shape = RoundedCornerShape(50.dp), // Match the shape of the Box
                    clip = false // Ensure the shadow is outside the bounds of the Box
                )
                .clip(RoundedCornerShape(50.dp))
                .background(contentColor.copy(alpha = 0.6f))
        )
    }
}
