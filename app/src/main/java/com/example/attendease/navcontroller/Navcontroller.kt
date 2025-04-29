package com.example.attendease.navcontroller

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.attendease.UserPreferences
import com.example.attendease.model.DetailViewModel
import com.example.attendease.model.MainViewModel
import com.example.attendease.model.PaymentViewModel
import com.example.attendease.model.SubjectViewModel
import com.example.attendease.model.TimetableViewModel
import com.example.attendease.screen.AttendanceDetailExpanded
import com.example.attendease.screen.DonateScreen
import com.example.attendease.screen.HomeScreen
import com.example.attendease.screen.SettingsScreen
import com.example.attendease.screen.TimeTableScreen
import com.example.attendease.uicomponent.bottomnavbar.Screen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    subjectViewModel: SubjectViewModel,
    timetableViewModel: TimetableViewModel,
    detailViewModel: DetailViewModel,
    userName: String,
    selectedColor: Int?,
    mainViewModel: MainViewModel,
    paymentViewModel: PaymentViewModel
) {
    val context = LocalContext.current


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
                viewModel4 = timetableViewModel,
                userPreference = UserPreferences(context)
            )
        }
        composable(Screen.Timetable.route){
            TimeTableScreen(selectedColor,navController,timetableViewModel,subjectViewModel, userPreference = UserPreferences(context))
        }
        composable(Screen.Donate.route) {
            DonateScreen(userName,selectedColor,navController,paymentViewModel)
        }
        composable(Screen.Settings.route){
            SettingsScreen(selectedColor,navController,subjectViewModel,timetableViewModel)
        }
        composable(
            route = "attendanceDetail/{subjectName}",
            arguments = listOf(navArgument("subjectName") { type = NavType.StringType })
        ) { backStackEntry ->
            val subjectName = backStackEntry.arguments?.getString("subjectName")
            AttendanceDetailExpanded(
                navController = navController,
                selectedColor = selectedColor,
                viewModel = detailViewModel,
                userPreference = UserPreferences(context),
                subject = subjectName ?: "",
            )
        }

    }
}