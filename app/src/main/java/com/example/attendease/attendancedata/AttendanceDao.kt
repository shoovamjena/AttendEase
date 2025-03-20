package com.example.attendease.attendancedata

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface AttendanceDao {

    @Insert
    fun addDetail(attendance: Attendance)


    @Query("SELECT * FROM attendance WHERE id= :subjectId ORDER BY dateTime DESC")
    fun getAllDetail(subjectId: Int): Flow<List<Attendance>>


    @Query("DELETE FROM attendance WHERE id = :subjectId")
    fun resetAttendance(subjectId:Int)

    @Query("SELECT * FROM attendance WHERE id = :subjectId")
    fun getSubjectById(subjectId: Int): Int
}