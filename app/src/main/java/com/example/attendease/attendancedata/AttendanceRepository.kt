package com.example.attendease.attendancedata

import kotlinx.coroutines.flow.Flow

class AttendanceRepository(
    private val attendanceDao: AttendanceDao
) {

    fun insertAttendanceRecord(subjectId: Int, status: String) {
        val currentTime = System.currentTimeMillis()
        val attendanceRecord = Attendance(
            id = subjectId,
            dateTime = currentTime,
            status = status,
        )
        attendanceDao.addDetail(attendanceRecord)  // Insert record
    }

     fun resetAttendance(subjectId: Int) { // Update subject details
        attendanceDao.resetAttendance(subjectId)  // Reset attendance
    }

    fun getAttendanceRecords(subjectId: Int): Flow<List<Attendance>> {
        return attendanceDao.getAllDetail(subjectId)
    }

    fun deleteDetail(attendId: Int){
        attendanceDao.deleteAttendance(attendId)
    }

    fun updateDetail(subjectId: Int, status: String){
        attendanceDao.updateDetail(subjectId,status)
    }

}

