package com.example.attendease.attendancedata

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Attendance::class], version = 1, exportSchema = false)
abstract class AttendanceDatabase : RoomDatabase() {
    abstract fun detailDao(): AttendanceDao

    companion object{
        @Volatile
        private var INSTANCE: AttendanceDatabase? = null

        fun getAttendanceDatabase(context: Context): AttendanceDatabase{
            return INSTANCE?: synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AttendanceDatabase::class.java,
                    "attendance_details"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}