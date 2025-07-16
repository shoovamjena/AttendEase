package com.ironheartproduction.attendelite.navcontroller

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.ironheartproduction.attendelite.UserPreferences
import com.ironheartproduction.attendelite.viewmodel.DetailViewModel
import com.ironheartproduction.attendelite.viewmodel.MainViewModel
import com.ironheartproduction.attendelite.viewmodel.PaymentViewModel
import com.ironheartproduction.attendelite.viewmodel.SubjectViewModel
import com.ironheartproduction.attendelite.viewmodel.TimetableViewModel
import com.ironheartproduction.attendelite.screen.AttendanceDetailExpanded
import com.ironheartproduction.attendelite.screen.DonateScreen
import com.ironheartproduction.attendelite.screen.HomeScreen
import com.ironheartproduction.attendelite.screen.SettingsScreen
import com.ironheartproduction.attendelite.screen.TimeTableScreen
import com.ironheartproduction.attendelite.uicomponent.bottomnavbar.Screen

@Composable
fun AppNavGraph(
    navController: NavHostController,
    subjectViewModel: SubjectViewModel,
    timetableViewModel: TimetableViewModel,
    detailViewModel: DetailViewModel,
    userName: String,
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
                viewModel = subjectViewModel,
                viewModel2 = detailViewModel,
                navController = navController,
                viewModel3 = mainViewModel,
                viewModel4 = timetableViewModel,
                userPreference = UserPreferences(context)
            )
        }
        composable(Screen.Timetable.route){
            TimeTableScreen(navController,timetableViewModel,subjectViewModel, userPreference = UserPreferences(context))
        }
        composable(Screen.Donate.route) {
            DonateScreen(userName,navController,paymentViewModel,UserPreferences(context))
        }
        composable(Screen.Settings.route){
            SettingsScreen(navController,subjectViewModel,timetableViewModel, userPreference = UserPreferences(context))
        }
        composable(
            route = "attendanceDetail/{subjectName}",
            arguments = listOf(navArgument("subjectName") { type = NavType.StringType })
        ) { backStackEntry ->
            val subjectName = backStackEntry.arguments?.getString("subjectName")
            AttendanceDetailExpanded(
                navController = navController,
                viewModel = detailViewModel,
                userPreference = UserPreferences(context),
                subject = subjectName ?: "",
            )
        }

    }
}