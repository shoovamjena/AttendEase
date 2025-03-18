package com.example.attendease.data

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "subject")
class Subject(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val attend: Int,
    val total: Int
){
    val attendancePercentage: Int
        get() = if (total > 0) (attend * 100) / total else 0

}
