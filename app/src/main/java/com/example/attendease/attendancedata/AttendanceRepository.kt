package com.example.attendease.attendancedata

import kotlinx.coroutines.flow.Flow

class AttendanceRepository(
    private val attendanceDao: AttendanceDao
) {

    fun insertAttendanceRecord(subjectId: Int, status: String) {
        val currentTime = System.currentTimeMillis()  // Get current timestamp
        val attendanceRecord = Attendance(
            id = subjectId,
            dateTime = currentTime,  // Store the timestamp
            status = status,// "Present" or "Absent"
        )
        attendanceDao.addDetail(attendanceRecord)  // Insert record
    }

     fun resetAttendance(subjectId: Int) { // Update subject details
        attendanceDao.resetAttendance(subjectId)  // Reset attendance
    }

    fun getAttendanceRecords(subjectId: Int): Flow<List<Attendance>> {
        return attendanceDao.getAllDetail(subjectId)
    }

}

