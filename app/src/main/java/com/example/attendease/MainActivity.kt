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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendease.ui.theme.AttendEaseTheme
import com.example.attendease.ui.theme.nothingFontFamily
import java.util.Calendar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AttendEaseTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    HomeScreen()
                }
            }
        }
    }
}



@Composable
fun HomeScreen() {
    val backgroundColor = MaterialTheme.colorScheme.primaryContainer
    val contentColor = MaterialTheme.colorScheme.primary

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor.copy(alpha = 0.2f))
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth()
                .padding(top=70.dp, start = 10.dp, end = 10.dp)
        ) {

            Column(modifier = Modifier.padding(top = 10.dp)
                .fillMaxSize()) {
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
                        onClick = { /* Handle icon click */ },
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
                    fontFamily = nothingFontFamily,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.alpha(0.5f)
                )
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .heightIn(min = 585.dp)// Height based on content
                        .padding(top = 25.dp, bottom = 30.dp)
                        .clip(RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp, topStart = 20.dp, topEnd = 20.dp))
                        .background(contentColor.copy(alpha = 0.1f))
                        .weight(1f)
                ){}

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
        ){
            IconButton(
                onClick = { /* Handle icon click */ },
                colors = IconButtonDefaults.iconButtonColors(backgroundColor),
                modifier = Modifier.size(56.dp)
                    .align(Alignment.Center)
                    .alpha(0.7f)
            ) {
                Icon(
                    imageVector = Icons.Default.AddCircle,
                    contentDescription = "Settings",
                    tint = contentColor.copy(alpha = 0.7f), // Icon color
                    modifier = Modifier.size(56.dp)
                )
            }
        }
    }
}
 @Composable
fun TimeBasedGreeting (){
    val currentTime = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
     val contentColor = MaterialTheme.colorScheme.primary
     val greetingMessage = when{
         currentTime in 4..11 -> "Good Morning"
         currentTime in 12..17 -> "Good Afternoon"
         currentTime in 18..22 -> "Good Evening"
         else -> "Good Night"
     }

     Text(
         text = greetingMessage,
         color = contentColor,
         fontSize = 32.sp,
         fontFamily = nothingFontFamily,
         fontWeight = FontWeight.ExtraBold,
         modifier = Modifier.alpha(0.6f)
     )

}