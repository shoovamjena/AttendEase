package com.example.attendease.navcontroller

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.attendease.model.DetailViewModel
import com.example.attendease.model.MainViewModel
import com.example.attendease.model.SubjectViewModel
import com.example.attendease.model.TimetableViewModel
import com.example.attendease.screen.DonateScreen
import com.example.attendease.screen.HomeScreen
import com.example.attendease.screen.SettingsScreen
import com.example.attendease.screen.TimeTableScreen
import com.example.attendease.uicomponent.bottomnavbar.Screen
import kotlinx.coroutines.flow.Flow

@Composable
fun AppNavGraph(
    navController: NavHostController,
    subjectViewModel: SubjectViewModel,
    timetableViewModel: TimetableViewModel,
    detailViewModel: DetailViewModel,
    userName: String,
    selectedColor: Int?,
    mainViewModel: MainViewModel
) {

    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                userName = userName,
                selectedColor = selectedColor,
                viewModel = subjectViewModel,
                viewModel2 = detailViewModel,
                navController = navController,
                viewModel3 = mainViewModel,
                viewModel4 = timetableViewModel
            )
        }
        composable(Screen.Timetable.route){
            TimeTableScreen(selectedColor,navController,timetableViewModel,subjectViewModel)
        }
        composable(Screen.Donate.route) {
            DonateScreen(selectedColor,navController)
        }
        composable(Screen.Settings.route){
            SettingsScreen(selectedColor,navController)
        }
    }
}