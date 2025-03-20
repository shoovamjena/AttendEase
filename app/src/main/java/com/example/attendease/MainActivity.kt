package com.example.attendease

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.attendease.attendancedata.AttendanceDatabase
import com.example.attendease.attendancedata.AttendanceRepository
import com.example.attendease.model.DetailViewModel
import com.example.attendease.model.SubjectViewModel
import com.example.attendease.model.SubjectViewModelFactory
import com.example.attendease.screen.ChooseColorScreen
import com.example.attendease.screen.HomeScreen
import com.example.attendease.screen.WelcomeScreen
import com.example.attendease.subjectdata.SubjectDatabase
import com.example.attendease.subjectdata.SubjectRepository
import com.example.attendease.ui.theme.DarkColorScheme
import com.example.attendease.ui.theme.LightColorScheme
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
                        AppTheme(
                            dynamicColor = storedColor == null // Dynamic only if no stored color
                        ) {
                            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && storedColor == null) {
                                ChooseColorScreen()
                            } else {
                                HomeScreen(
                                    name, storedColor, subjectViewModel, detailViewModel
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AppTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true, // ✅ Wallpaper-based theming on Android 12+
    selectedColor: Int? = null, // ✅ Selected color from ChooseColorScreen for Android 11 and below
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context)
            else dynamicLightColorScheme(context) // ✅ Uses wallpaper-based theme colors
        }
        darkTheme -> DarkColorScheme
        selectedColor != null -> lightColorScheme( // ✅ Uses chosen color from ChooseColorScreen
            primary = Color(selectedColor)
        )
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}



