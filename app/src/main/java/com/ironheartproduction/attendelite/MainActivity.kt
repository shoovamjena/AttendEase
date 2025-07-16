package com.ironheartproduction.attendelite

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
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.ironheartproduction.attendelite.model.attendancedata.AttendanceDatabase
import com.ironheartproduction.attendelite.model.attendancedata.AttendanceRepository
import com.ironheartproduction.attendelite.model.subjectdata.SubjectDatabase
import com.ironheartproduction.attendelite.model.subjectdata.SubjectRepository
import com.ironheartproduction.attendelite.model.timetabledata.TimetableDatabase
import com.ironheartproduction.attendelite.model.timetabledata.TimetableRepository
import com.ironheartproduction.attendelite.navcontroller.AppNavGraph
import com.ironheartproduction.attendelite.notification.AlarmScheduler
import com.ironheartproduction.attendelite.screen.WelcomeScreen
import com.ironheartproduction.attendelite.ui.theme.AttendEliteTheme
import com.ironheartproduction.attendelite.ui.theme.ThemePreference
import com.ironheartproduction.attendelite.viewmodel.DetailViewModel
import com.ironheartproduction.attendelite.viewmodel.MainViewModel
import com.ironheartproduction.attendelite.viewmodel.PaymentViewModel
import com.ironheartproduction.attendelite.viewmodel.SubjectViewModel
import com.ironheartproduction.attendelite.viewmodel.TimetableViewModel
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

        val subjectViewModel = SubjectViewModel(subjectRepository)
        val detailViewModel = DetailViewModel(attendanceRepository,subjectRepository)
        val timeTableViewModel = TimetableViewModel(timetableRepository)
        alarmScheduler = AlarmScheduler(this)
        fun startNotificationService() {
            AlarmScheduler(context = applicationContext).scheduleAllAlarms()
        }
        fun showExactAlarmPermissionDialog() {
            AlertDialog.Builder(this)
                .setTitle("Permission Required")
                .setMessage("To ensure you receive timely notifications about your upcoming classes, AttendElite needs permission to schedule exact alarms.")
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
            checkNotificationPermission()

            if (name.isNullOrEmpty()) {
                setContent {
                    WelcomeScreen(onNavigateToMain = { recreate() })
                }
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
                    AttendEliteTheme(
                        darkTheme = isDarkTheme,
                        dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S,
                    ) {
                        AppNavGraph(
                            navController = navController,
                            userName = name,
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

