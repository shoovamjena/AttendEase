package com.example.attendease.uicomponent

import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.attendease.R
import com.example.attendease.screen.WelcomeScreen
import com.example.attendease.ui.theme.nothingFontFamily

@Composable
fun welcomeButton(
    modifier: Modifier = Modifier,
    text: String,
    isNavigationArrowVisible: Boolean,
    onClicked: () -> Unit,
    colors: ButtonColors,
    shadowColor: Color
){
    val isAndroid12OrAbove = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
    Button(
        modifier = modifier
            .fillMaxWidth()
            .height(62.dp)
            .padding(horizontal = 15.dp)
            .then(
                if (isAndroid12OrAbove) Modifier.shadow(
                    elevation = 24.dp,
                    shape = RoundedCornerShape(percent = 50),
                    spotColor = shadowColor
                ) else Modifier // No shadow for Android 11 or below
            ),
        onClick = onClicked,
        colors = colors) {
        Row (
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            Text(
                text = text,
                fontFamily = nothingFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 24.sp
            )
            if(isNavigationArrowVisible){
                Icon(
                    painter = painterResource(R.drawable.arrow),
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

