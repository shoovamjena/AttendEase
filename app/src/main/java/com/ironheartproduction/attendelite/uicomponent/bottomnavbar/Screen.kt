package com.ironheartproduction.attendelite.uicomponent.bottomnavbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.ironheartproduction.attendelite.R

sealed class Screen(
    val title: String,
    val activeIcon: @Composable () -> ImageVector,
    val inactiveIcon: @Composable () -> ImageVector,
    val route: String
) {
    data object Home: Screen("Home", { Icons.Filled.Home }, { Icons.Outlined.Home },"home")
    data object Timetable: Screen("Timetable",
        { ImageVector.vectorResource(R.drawable.calendar_filled) },
        { ImageVector.vectorResource(R.drawable.calendar_outline) },"timetable")
    data object Donate: Screen("Support", { ImageVector.vectorResource(R.drawable.donate_filled) },
        { ImageVector.vectorResource(R.drawable.donate_outline) },"donate")
    data object Settings: Screen("Settings", { Icons.Filled.Settings }, { Icons.Outlined.Settings },"settings")
}