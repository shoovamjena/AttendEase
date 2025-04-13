package com.example.attendease

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.compose.rememberNavController
import com.example.attendease.attendancedata.AttendanceDatabase
import com.example.attendease.attendancedata.AttendanceRepository
import com.example.attendease.model.DetailViewModel
import com.example.attendease.model.SubjectViewModel
import com.example.attendease.model.SubjectViewModelFactory
import com.example.attendease.navcontroller.AppNavGraph
import com.example.attendease.screen.ChooseColorScreen
import com.example.attendease.screen.WelcomeScreen
import com.example.attendease.subjectdata.SubjectDatabase
import com.example.attendease.subjectdata.SubjectRepository
import com.example.attendease.ui.theme.AttendEaseTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch


class MainActivity : ComponentActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val userPreferences = UserPreferences(this)
        // Initialize Room Database
        val database = SubjectDatabase.getDatabase(applicationContext)
        val database2 = AttendanceDatabase.getAttendanceDatabase(applicationContext)


        // Initialize Repository
        val repository = SubjectRepository(database.subjectDao())
        val detailRepository = AttendanceRepository(database2.detailDao())

        // Initialize ViewModel using Factory
        val viewModelFactory = SubjectViewModelFactory(repository)
        val subjectViewModel = ViewModelProvider(this, viewModelFactory)[SubjectViewModel::class.java]

        val detailViewModel = DetailViewModel(detailRepository)

        lifecycleScope.launch {
            val name = userPreferences.getUserName.first() // Get stored name
            val storedColor = userPreferences.getThemeColor()


            if (name.isNullOrEmpty()) {
                // Show Welcome Screen if name is not set
                setContent {
                    WelcomeScreen(
                        onNavigateToMain = { recreate() } // Refresh to load Main UI
                    )
                }
            } else {
                // Show Main Screen with stored name
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && storedColor == null) {
                    setContent {
                        ChooseColorScreen()
                    }
                } else {
                    // Android 12+ uses Material You, Android < 12 uses selected color
                    setContent {
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
                            )
                        }

                    }

                }
            }
        }
    }
}




