package com.example.attendease.uicomponent.bottomnavbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.example.attendease.R

sealed class Screen(
    val title: String,
    val activeIcon: @Composable () -> ImageVector,
    val inactiveIcon: @Composable () -> ImageVector,
    val route: String
) {
    object Home: Screen("Home", { Icons.Filled.Home }, { Icons.Outlined.Home },"home")
    object Timetable: Screen("Timetable",
        { ImageVector.vectorResource(R.drawable.calendar_filled) },
        { ImageVector.vectorResource(R.drawable.calendar_outline) },"timetable")
    object Donate: Screen("Support", { ImageVector.vectorResource(R.drawable.donate_filled) },
        { ImageVector.vectorResource(R.drawable.donate_outline) },"donate")
    object Settings: Screen("Settings", { Icons.Filled.Settings }, { Icons.Outlined.Settings },"settings")
}