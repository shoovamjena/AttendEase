package com.ironheartproduction.attendelite.screen

import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ironheartproduction.attendelite.R
import com.ironheartproduction.attendelite.UserPreferences
import com.ironheartproduction.attendelite.ui.theme.nothingFontFamily
import com.ironheartproduction.attendelite.uicomponent.WelcomeButton
import kotlinx.coroutines.launch


@Composable
fun WelcomeScreen(
    modifier: Modifier = Modifier,
    onNavigateToMain: () -> Unit

){
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()
    val keyboardHeight = WindowInsets.ime.getBottom(LocalDensity.current)

    var name by remember { mutableStateOf("") }

    val gradientOffset by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 4000, easing = LinearEasing), label = "",
    )
    val bgColor = MaterialTheme.colorScheme.primaryContainer
    val textColor = MaterialTheme.colorScheme.secondaryContainer
    val gradientColors = listOf(bgColor,textColor)
    val animatedBrush = Brush.verticalGradient(
        colors = gradientColors,
        startY = 0f,
        endY = with(LocalDensity.current) { 1000.dp.toPx() * gradientOffset },
        tileMode = TileMode.Clamp
    )

    val context = LocalContext.current
    val userPreferences = remember { UserPreferences(context) }
    LaunchedEffect(keyboardHeight) {
        coroutineScope.launch{
            scrollState.scrollBy(keyboardHeight.toFloat())
        }
    }

    Column (
        modifier = modifier
            .fillMaxSize()
            .background(animatedBrush)
            .systemBarsPadding()
            .verticalScroll(scrollState)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally
    ){

        Image(
            painter = painterResource(R.drawable.welcome),
            contentDescription = null,
            modifier = Modifier.size(250.dp)
        )
        Spacer(
            modifier = modifier.height(10.dp)
        )
        Text(
            text = "Ready to catch up with your attendance",
            color = MaterialTheme.colorScheme.primary,
            fontSize = 32.sp,
            fontFamily = nothingFontFamily,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(20.dp)
        )
        Spacer(
            modifier = modifier.height(30.dp)
        )
        Text(
            text = "May I Know your Name, Please!",
            color = MaterialTheme.colorScheme.tertiary,
            fontSize = 22.sp,
            fontFamily = nothingFontFamily,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(20.dp)
        )
        Spacer(
            modifier = modifier.height(10.dp)
        )
        InputField(
            leadingIconRes = R.drawable.person,
            placeholderText = "Your Name",
            inputValue = name,
            onInputChange = { name = it.take(20).uppercase() },
            modifier = modifier.padding(horizontal = 24.dp)
        )
        Spacer(
            modifier = modifier.height(80.dp)
        )

        WelcomeButton(
            text = "GET STARTED!!",
            isNavigationArrowVisible = true,
            onClicked = {
                if (name.isNotEmpty()) {
                    coroutineScope.launch {
                        userPreferences.saveUserName(name)
                        onNavigateToMain() // Navigate to MainActivity
                    }
                } else {
                    Toast.makeText(context, "Please enter your name!", Toast.LENGTH_SHORT).show()
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f),
                contentColor = Color.White
            ),
            shadowColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(horizontal = 24.dp)
        )

    }
}



@Composable
private fun InputField(
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    @DrawableRes leadingIconRes: Int,
    placeholderText: String,
    inputValue: String,
    onInputChange: (String) -> Unit
){
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .height(62.dp),
        value = inputValue,
        onValueChange = onInputChange,
        visualTransformation = visualTransformation,
        singleLine = true,
        shape = RoundedCornerShape(percent = 50),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            errorIndicatorColor = Color.Transparent,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            unfocusedPlaceholderColor = Color.Black,
            focusedPlaceholderColor = Color.Black.copy(alpha = 0.5f),
            focusedLeadingIconColor = Color.Black,
            unfocusedLeadingIconColor = Color.Black,
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White.copy(alpha = 0.7f),
        ),
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            fontWeight = FontWeight.Medium
        ),
        leadingIcon = {
            Icon(
                painter = painterResource(leadingIconRes),
                contentDescription = null,
                modifier = Modifier.size(24.dp))
        },
        placeholder = {
            Text(text = placeholderText)
        }
    )
}

