package com.example.attendease.timetabledata

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Timetable")
data class Timetable (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val subjectName: String,
    val startTime: String,
    val endTime: String,
    val day: String
)