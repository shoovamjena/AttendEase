package com.example.attendease.model.subjectdata

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SubjectDao {

    @Insert
     fun addSubject(subject: Subject)

    @Update
     fun editSubject(subject: Subject)

    @Delete
     fun deleteSubject(subject: Subject)

    @Query("SELECT * FROM subject ORDER BY id DESC")
     fun getAllSubjects(): Flow<List<Subject>>

    @Query("UPDATE subject SET attend = attend + 1, total = total + 1 WHERE id = :id")
     fun markPresent(id: Int)

    @Query("UPDATE subject SET total = total + 1 WHERE id = :id")
     fun markAbsent(id: Int)

     @Query("UPDATE subject SET attend=0,total = 0 WHERE id = :id")
     fun resetAttendance(id:Int)

     @Query("DELETE FROM subject")
     fun resetSubjects()

    @Query("SELECT * FROM subject WHERE id = :subjectId")
    fun getSubjectById(subjectId: Int): Subject

    @Query("UPDATE subject SET attend = attend + 1 WHERE id = :id")
    fun markPresentAttendance(id: Int)

    @Query("UPDATE subject SET attend = attend - 1 WHERE id = :id")
    fun markAbsentAttendance(id: Int)

    @Query("UPDATE subject SET attend = MAX(attend - 1, 0), total = MAX(total - 1, 0) WHERE id = :id")
    fun deletePresentAttendance(id: Int)

    @Query("UPDATE subject SET total = MAX(total - 1, 0) WHERE id = :id")
    fun deleteAbsentAttendance(id: Int)
}