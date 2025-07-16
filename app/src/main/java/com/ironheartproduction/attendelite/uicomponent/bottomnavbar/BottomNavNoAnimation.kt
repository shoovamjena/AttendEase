package com.ironheartproduction.attendelite.uicomponent.bottomnavbar

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun BottomNavNoAnimation(
    screens: List<Screen>,
    color: Color,
    color2: Color,
    navController: NavController,
    currentScreen: Int
) {
    var selectedScreen by remember { mutableIntStateOf(currentScreen) }

    Box(
        Modifier
            .shadow(5.dp)
            .background(color)
            .height(64.dp)
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            screens.forEachIndexed { index, screen ->
                val isSelected = index == selectedScreen

                val animatedWeight by animateFloatAsState(
                    targetValue = if (isSelected) 1.5f else 1f, label = ""
                )

                val interactionSource = remember { MutableInteractionSource() }

                Box(
                    modifier = Modifier
                        .weight(animatedWeight)
                        .clickable(
                            interactionSource = interactionSource,
                            indication = null
                        ) {
                            if (selectedScreen != index) {
                                selectedScreen = index
                                navController.navigate(screen.route) {
                                    launchSingleTop = true
                                    restoreState = true
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                }
                            }
                        },
                    contentAlignment = Alignment.TopCenter
                ) {
                    BottomNavItem(
                        modifier = Modifier,
                        screen = screen,
                        isSelected = isSelected,
                        color = color,
                        color2 = color2
                    )
                }
            }
        }
    }
}
