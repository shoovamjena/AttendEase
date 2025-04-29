package com.example.attendease

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.attendease.attendancedata.AttendanceDatabase
import com.example.attendease.attendancedata.AttendanceRepository
import com.example.attendease.model.DetailViewModel
import com.example.attendease.model.MainViewModel
import com.example.attendease.model.PaymentViewModel
import com.example.attendease.model.SubjectViewModel
import com.example.attendease.model.SubjectViewModelFactory
import com.example.attendease.model.TimetableViewModel
import com.example.attendease.navcontroller.AppNavGraph
import com.example.attendease.notification.AlarmScheduler
import com.example.attendease.notification.NotificationForegroundService
import com.example.attendease.screen.ChooseColorScreen
import com.example.attendease.screen.WelcomeScreen
import com.example.attendease.subjectdata.SubjectDatabase
import com.example.attendease.subjectdata.SubjectRepository
import com.example.attendease.timetabledata.TimetableDatabase
import com.example.attendease.timetabledata.TimetableRepository
import com.example.attendease.ui.theme.AttendEaseTheme
import com.example.attendease.ui.theme.ThemePreference
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity(), PaymentResultWithDataListener{
    private val paymentViewModel: PaymentViewModel by viewModels()
    private lateinit var alarmScheduler: AlarmScheduler
    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(this, "Notification permission denied.", Toast.LENGTH_SHORT).show()
            }
        }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    android.Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val userPreferences = UserPreferences(this)

        val subjectDb = SubjectDatabase.getDatabase(applicationContext)
        val attendanceDb = AttendanceDatabase.getAttendanceDatabase(applicationContext)
        val timetableDb = TimetableDatabase.getDatabase(applicationContext)

        val subjectRepository = SubjectRepository(subjectDb.subjectDao())
        val attendanceRepository = AttendanceRepository(attendanceDb.detailDao())
        val timetableRepository = TimetableRepository(timetableDb.timetableDao())

        val viewModelFactory = SubjectViewModelFactory(subjectRepository)
        val subjectViewModel = ViewModelProvider(this, viewModelFactory)[SubjectViewModel::class.java]
        val detailViewModel = DetailViewModel(attendanceRepository,subjectRepository)
        val timeTableViewModel = TimetableViewModel(timetableRepository)
        alarmScheduler = AlarmScheduler(this)
        fun startNotificationService() {
            NotificationForegroundService.startService(this)
        }
        fun showExactAlarmPermissionDialog() {
            AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("To ensure you receive timely notifications about your upcoming classes, AttendEase needs permission to schedule exact alarms.")
                .setPositiveButton("Grant Permission") { _, _ ->
                    // Request the permission
                    alarmScheduler.requestExactAlarmPermission(this)
                }
                .setNegativeButton("Later") { _, _ ->
                    // Fall back to inexact alarms
                    startNotificationService()
                }
                .setCancelable(false)
                .show()
        }
        // Check for exact alarm permission
        if (!alarmScheduler.canScheduleExactAlarms()) {
            // Show dialog to explain why we need permission
            showExactAlarmPermissionDialog()
        } else {
            // Start the service as we have permission
            startNotificationService()
        }


        lifecycleScope.launch {
            val name = userPreferences.getUserName.first()
            val storedColor = userPreferences.getThemeColor()
            checkNotificationPermission()

            if (name.isNullOrEmpty()) {
                setContent {
                    WelcomeScreen(onNavigateToMain = { recreate() })
                }
            } else {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S && storedColor == null) {
                    setContent { ChooseColorScreen() }
                } else {
                    setContent {
                        val themePreference by userPreferences.themePreferenceFlow.collectAsState(
                            initial = ThemePreference.SYSTEM_DEFAULT
                        )
                        val isDarkTheme = when (themePreference) {
                            ThemePreference.LIGHT -> false
                            ThemePreference.DARK -> true
                            ThemePreference.SYSTEM_DEFAULT -> isSystemInDarkTheme()
                        }
                        val viewModel: MainViewModel = viewModel()
                        val navController = rememberNavController()
                        AttendEaseTheme(
                            darkTheme = isDarkTheme,
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
                                mainViewModel = viewModel,
                                paymentViewModel = paymentViewModel
                            )
                        }
                    }
                }
            }
        }
    }

    override fun onPaymentSuccess(razorpayPaymentID: String?, paymentData: PaymentData?) {
        if (razorpayPaymentID != null) {
            paymentViewModel.handlePaymentSuccess(razorpayPaymentID)
        }
    }

    override fun onPaymentError(code: Int, response: String?, paymentData: PaymentData?) {
        if (response != null) {
            paymentViewModel.handlePaymentError()
        }
    }
}

