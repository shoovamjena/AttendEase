package com.example.attendease.attendancedata

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Attendance")
class Attendance (
    @PrimaryKey(autoGenerate = true) val attendId: Int = 0,
    val id: Int,
    val dateTime: Long = System.currentTimeMillis(),
    val status: String
)
