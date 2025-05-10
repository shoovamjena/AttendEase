package com.example.attendease.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.PowerManager
import com.example.attendease.UserPreferences
import com.example.attendease.subjectdata.SubjectDatabase
import com.example.attendease.timetabledata.TimetableDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Locale

class ClassCheckerReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {

        val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
        val wakeLock = powerManager.newWakeLock(
            PowerManager.PARTIAL_WAKE_LOCK,
            "AttendEase:ClassCheckWakeLock"
        )

        wakeLock.acquire(3 * 60 * 1000L) // 3 minutes max

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val dayOfWeek = Calendar.getInstance()
                    .getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, Locale.ENGLISH)
                    ?.uppercase() ?: return@launch

                val timetableDao = TimetableDatabase.getDatabase(context).timetableDao()
                val classesForToday = timetableDao.getClassesByDate(dayOfWeek)

                val userPreferences = UserPreferences(context)
                val targetAttendance = userPreferences.targetFlow.first()

                val subjectDao = SubjectDatabase.getDatabase(context).subjectDao()
                val allSubjects = subjectDao.getAllSubjects().first()

                val formatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH)
                val currentTimeParsed = LocalTime.now()

                for (timetableEntry in classesForToday) {
                    val classTimeParsed = LocalTime.parse(timetableEntry.startTime, formatter)
                    val diffInMinutes = ChronoUnit.MINUTES.between(currentTimeParsed, classTimeParsed)


                    // Check if class is within 1-30 minutes
                    if (diffInMinutes in 1..30) {
                        val subject = allSubjects.find { it.name == timetableEntry.subjectName }

                        if (subject != null) {
                            val notificationHelper = NotificationHelper(context)
                            notificationHelper.showClassNotification(
                                classId = timetableEntry.id,
                                subjectName = subject.name,
                                startTime = timetableEntry.startTime,
                                attendancePercentage = subject.attendancePercentage,
                                targetPercentage = targetAttendance
                            )
                        }
                    }
                }
            } finally {
                if (wakeLock.isHeld) {
                    wakeLock.release()
                }
            }
        }
    }
}