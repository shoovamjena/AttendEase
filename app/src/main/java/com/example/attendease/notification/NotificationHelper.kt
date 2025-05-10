package com.example.attendease.notification

import android.app.Activity
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.PowerManager
import android.os.SystemClock
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.attendease.R
import com.example.attendease.UserPreferences
import com.example.attendease.subjectdata.SubjectDatabase
import com.example.attendease.timetabledata.TimetableDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class NotificationForegroundService : Service() {

    companion object {
        const val CHANNEL_ID = "foreground_service_channel"
        const val NOTIFICATION_ID = 9999

        fun startService(context: Context) {
            val startServiceIntent = Intent(context, NotificationForegroundService::class.java)
            context.startForegroundService(startServiceIntent)
        }

    }

    override fun onCreate() {
        super.onCreate()
        createForegroundNotificationChannel()
    }

    private fun createForegroundNotificationChannel() {
        val name = "AttendEase Service"
        val descriptionText = "Keeps AttendEase running to check for upcoming classes"
        val importance = NotificationManager.IMPORTANCE_LOW
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
        }
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("AttendEase Active")
            .setContentText("Monitoring upcoming classes")
            .setSmallIcon(R.drawable.ae_logo)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        // Start foreground service with explicit type for Android 14+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {  // API 34
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        // Schedule notifications
        AlarmScheduler(this).scheduleAllAlarms()

        return START_STICKY
    }

    override fun onBind(intent: Intent?) = null

}

class NotificationHelper(private val context: Context) {

    companion object {
        const val CHANNEL_ID = "class_notification_channel"
        const val NOTIFICATION_REQUEST_CODE = 123
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        val name = "Class Notifications"
        val descriptionText = "Notifications for upcoming classes"
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
            description = descriptionText
            enableVibration(true)
            enableLights(true)
            setShowBadge(true)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    fun showClassNotification(classId: Int, subjectName: String, startTime: String, attendancePercentage: Int, targetPercentage: Float) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        intent?.apply {
            putExtra("classId", classId)
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            classId,  // Use unique IDs for different notifications
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ae_logo)
            .setContentTitle("Upcoming Class: $subjectName at $startTime")
            .setContentText(getAttendanceMessage(currentAttendance = attendancePercentage, targetAttendance = targetPercentage))
            .setPriority(NotificationCompat.PRIORITY_MAX)  // Critical for visibility when app is closed
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setFullScreenIntent(pendingIntent, true)  // This shows as a high-priority notification even when the device is locked
            .build()

        NotificationManagerCompat.from(context).apply {
            try {
                notify(classId, notification)
            } catch (_: SecurityException) {}
        }
    }

    fun cancelAllNotifications() {
        NotificationManagerCompat.from(context).cancelAll()
    }

}

// This receiver handles class notification alarms
class ClassNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        // Acquire wake lock to ensure work completes even if device is in Doze mode
        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "AttendEase:ClassNotificationWakeLock"
        )

        wakeLock.acquire(5 * 60 * 1000L) // 5 minutes max

        try {
            // Get data from intent
            val classId = intent.getIntExtra("classId", -1)
            val subjectName = intent.getStringExtra("subjectName") ?: "Unknown"
            val startTime = intent.getStringExtra("startTime") ?: "Unknown"
            val attendancePercentage = intent.getIntExtra("attendancePercentage", 0)
            val targetPercentage = intent.getFloatExtra("targetPercentage", 75f)

            // Show the notification
            val notificationHelper = NotificationHelper(context)
            notificationHelper.showClassNotification(
                classId = classId,
                subjectName = subjectName,
                startTime = startTime,
                attendancePercentage = attendancePercentage,
                targetPercentage = targetPercentage
            )

        }
        finally {
            // Release wake lock
            if (wakeLock.isHeld) {
                wakeLock.release()
            }
        }
    }
}

// This receiver handles midnight reset alarms
class MidnightNotificationReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "AttendEase:MidnightWakeLock"
        )

        wakeLock.acquire(3 * 60 * 1000L) // 3 minutes max

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dayOfWeek = Calendar.getInstance()
                    .getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH)
                    ?.uppercase() ?: return@launch

                val timetableDao = TimetableDatabase.getDatabase(context).timetableDao()
                val classesForToday = timetableDao.getClassesByDate(dayOfWeek)

                // Cancel any previous notifications
                val notificationHelper = NotificationHelper(context)
                notificationHelper.cancelAllNotifications()

                if (classesForToday.isNotEmpty()) {
                    val firstClass = classesForToday.minByOrNull {
                        val sdf = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
                        sdf.parse(it.startTime)?.time ?: Long.MAX_VALUE
                    }

                    val lastClass = classesForToday.maxByOrNull {
                        val sdf = SimpleDateFormat("hh:mm a", Locale.ENGLISH)
                        sdf.parse(it.startTime)?.time ?: Long.MIN_VALUE
                    }

                    if (firstClass != null && lastClass != null) {
                        createDailyScheduleNotification(
                            context,
                            dayOfWeek,
                            classesForToday.size,
                            firstClass.startTime,
                            lastClass.endTime
                        )
                    }
                }

                // Schedule today's class notifications
                AlarmScheduler(context).scheduleClassAlarmsForToday()

            } finally {
                if (wakeLock.isHeld) {
                    wakeLock.release()
                }
            }
        }
    }

    private fun createDailyScheduleNotification(context: Context, day: String, classCount: Int, firstClassTime: String, lastClassTime: String) {
        val intent = context.packageManager.getLaunchIntentForPackage(context.packageName)

        val pendingIntent = PendingIntent.getActivity(
            context,
            NotificationHelper.NOTIFICATION_REQUEST_CODE,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(context, NotificationHelper.CHANNEL_ID)
            .setSmallIcon(R.drawable.ae_logo)
            .setContentTitle("Today's Classes ($day)")
            .setContentText("You have $classCount classes today, from $firstClassTime to $lastClassTime")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).apply {
            try {
                notify(1000, notification)
            } catch (_: SecurityException) {
            }
        }
    }
}

// Boot completed receiver to reschedule alarms after device restart
class BootCompletedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {

            // Start foreground service if needed
            NotificationForegroundService.startService(context)

            // Or just reschedule alarms without service
            // AlarmScheduler(context).scheduleAllAlarms()
        }
    }
}

// Main class to handle all alarm scheduling
class AlarmScheduler(private val context: Context) {

    private val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    // Check if we can schedule exact alarms
    fun canScheduleExactAlarms(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            alarmManager.canScheduleExactAlarms()
        } else {
            true // Before Android 12, permission wasn't required
        }
    }

    // Request permission to schedule exact alarms
    fun requestExactAlarmPermission(activity: Activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                val intent = Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
                activity.startActivity(intent)
            }
        }
    }

    // Call this to schedule all necessary alarms
    fun scheduleAllAlarms() {
        if (!canScheduleExactAlarms()) {
            // Fallback to inexact alarms or notify user
            scheduleFallbackAlarms()
            return
        }

        // Schedule midnight refresh alarm
        scheduleMidnightAlarm()

        // Schedule class notifications for today
        scheduleClassAlarmsForToday()
    }

    // Fallback to inexact alarms when permission not granted
    private fun scheduleFallbackAlarms() {
        // Schedule a repeating inexact alarm to check classes
        val intent = Intent(context, ClassCheckerReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            2000,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // Check every 15 minutes
        alarmManager.setRepeating(
            AlarmManager.ELAPSED_REALTIME_WAKEUP,
            SystemClock.elapsedRealtime() + TimeUnit.MINUTES.toMillis(1), // Start after 1 minute
            TimeUnit.MINUTES.toMillis(30),
            pendingIntent
        )

    }

    // Schedules the midnight alarm to reset and plan for next day
    private fun scheduleMidnightAlarm() {
        try {
            // Calculate time until midnight
            val calendar = Calendar.getInstance()
            calendar.apply {
                add(Calendar.DAY_OF_YEAR, 1)
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 1) // 12:01 AM to ensure it's just after midnight
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }

            val midnight = calendar.timeInMillis

            // Create intent for midnight alarm
            val intent = Intent(context, MidnightNotificationReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                1001, // Unique request code for midnight alarm
                intent,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )

            // Set repeating alarm for midnight
            try {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    midnight,
                    pendingIntent
                )
            } catch (se: SecurityException) {
                // Fall back to inexact alarm
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    midnight,
                    pendingIntent
                )
            }


        } catch (_: Exception) {
        }
    }

    // Schedule all class notifications for today
    fun scheduleClassAlarmsForToday() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Get today's day of week
                val dayOfWeek = Calendar.getInstance()
                    .getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH)
                    ?.uppercase() ?: return@launch

                // Get all classes for today
                val timetableDao = TimetableDatabase.getDatabase(context).timetableDao()
                val classesForToday = timetableDao.getClassesByDate(dayOfWeek)

                // Get target attendance percentage
                val userPreferences = UserPreferences(context)
                val targetAttendance = userPreferences.targetFlow.first()

                // Get all subjects for attendance data
                val subjectDao = SubjectDatabase.getDatabase(context).subjectDao()
                val allSubjects = subjectDao.getAllSubjects().first()

                // Current time
                val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
                val currentTimeParsed = LocalTime.now()

                // For each class, schedule notification 15-30 minutes before
                for (timetableEntry in classesForToday) {
                    try {
                        val classTimeParsed = LocalTime.parse(timetableEntry.startTime, formatter)

                        // Only schedule upcoming classes (not past ones)
                        if (classTimeParsed.isAfter(currentTimeParsed)) {
                            // Schedule notification 15 minutes before class
                            val notificationTime = classTimeParsed.minusMinutes(15)

                            // If notification time is still in the future
                            if (notificationTime.isAfter(currentTimeParsed)) {
                                val subject =
                                    allSubjects.find { it.name == timetableEntry.subjectName }

                                if (subject != null) {
                                    // Calculate alarm time
                                    val calendar = Calendar.getInstance()
                                    calendar.set(Calendar.HOUR_OF_DAY, notificationTime.hour)
                                    calendar.set(Calendar.MINUTE, notificationTime.minute)
                                    calendar.set(Calendar.SECOND, 0)
                                    calendar.set(Calendar.MILLISECOND, 0)

                                    // Create intent with all necessary class data
                                    val intent = Intent(
                                        context,
                                        ClassNotificationReceiver::class.java
                                    ).apply {
                                        putExtra("classId", timetableEntry.id)
                                        putExtra("subjectName", subject.name)
                                        putExtra("startTime", timetableEntry.startTime)
                                        putExtra(
                                            "attendancePercentage",
                                            subject.attendancePercentage
                                        )
                                        putExtra("targetPercentage", targetAttendance)
                                    }

                                    val pendingIntent = PendingIntent.getBroadcast(
                                        context,
                                        timetableEntry.id, // Use class ID as unique request code
                                        intent,
                                        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
                                    )

                                    // Set exact alarm with proper permission handling
                                    try {
                                        alarmManager.setExactAndAllowWhileIdle(
                                            AlarmManager.RTC_WAKEUP,
                                            calendar.timeInMillis,
                                            pendingIntent
                                        )
                                    } catch (se: SecurityException) {
                                        // Fall back to inexact alarm
                                        alarmManager.set(
                                            AlarmManager.RTC_WAKEUP,
                                            calendar.timeInMillis,
                                            pendingIntent
                                        )

                                    }
                                }
                            }
                        }
                    } catch (_: Exception) {
                    }
                }
            } catch (_: Exception) {
            }
        }
    }
}