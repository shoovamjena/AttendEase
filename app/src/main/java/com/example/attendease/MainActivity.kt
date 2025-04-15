package com.example.attendease

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.attendease.attendancedata.AttendanceDatabase
import com.example.attendease.attendancedata.AttendanceRepository
import com.example.attendease.model.DetailViewModel
import com.example.attendease.model.MainViewModel
import com.example.attendease.model.SubjectViewModel
import com.example.attendease.model.SubjectViewModelFactory
import com.example.attendease.model.TimetableViewModel
import com.example.attendease.navcontroller.AppNavGraph
import com.example.attendease.screen.ChooseColorScreen
import com.example.attendease.screen.WelcomeScreen
import com.example.attendease.subjectdata.SubjectDatabase
import com.example.attendease.subjectdata.SubjectRepository
import com.example.attendease.timetabledata.TimetableDatabase
import com.example.attendease.timetabledata.TimetableRepository
import com.example.attendease.ui.theme.AttendEaseTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val userPreferences = UserPreferences(this)

        val database = SubjectDatabase.getDatabase(applicationContext)
        val database2 = AttendanceDatabase.getAttendanceDatabase(applicationContext)
        val database3 = TimetableDatabase.getDatabase(applicationContext)

        val repository = SubjectRepository(database.subjectDao())
        val detailRepository = AttendanceRepository(database2.detailDao())
        val timetableRepository = TimetableRepository(database3.timetableDao())

        val viewModelFactory = SubjectViewModelFactory(repository)
        val subjectViewModel = ViewModelProvider(this, viewModelFactory)[SubjectViewModel::class.java]
        val detailViewModel = DetailViewModel(detailRepository)
        val timeTableViewModel = TimetableViewModel(timetableRepository)

        lifecycleScope.launch {
            val name = userPreferences.getUserName.first()
            val storedColor = userPreferences.getThemeColor()


            if (name.isNullOrEmpty()) {

                setContent {
                    WelcomeScreen(
                        onNavigateToMain = { recreate() }
                    )
                }
            } else {

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && storedColor == null) {
                    setContent {
                        ChooseColorScreen()
                    }
                } else {
                    // Android 12+ uses Material You, Android < 12 uses selected color
                    setContent {
                        val viewModel: MainViewModel = viewModel()
                        val navController = rememberNavController()
                        AttendEaseTheme(
                            dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && storedColor == null,
                            selectedColor = storedColor
                        ) {
                            AppNavGraph(
                                navController = navController,
                                userName = name,
                                selectedColor = storedColor,
                                subjectViewModel = subjectViewModel,
                                detailViewModel = detailViewModel,
                                timetableViewModel = timeTableViewModel,
                                mainViewModel = viewModel
                            )
                        }

                    }

                }
            }
        }
    }
}




