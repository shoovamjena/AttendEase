package com.example.attendease.timetabledata

import androidx.room.*

@Dao
interface TimetableDao {
    @Query("SELECT * FROM timetable WHERE day = :date order by startTime ASC")
    fun getClassesByDate(date: String): List<Timetable>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertClass(timetable: Timetable)

    @Delete
    fun deleteClass(timetable: Timetable)

    @Update
    fun updateClass(timetable: Timetable)
}