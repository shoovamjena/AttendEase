package com.ironheartproduction.attendelite.model.attendancedata

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
        attendanceDao.addDetail(attendanceRecord)
    }

     fun resetAttendance(subjectId: Int) {
        attendanceDao.resetAttendance(subjectId)
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

