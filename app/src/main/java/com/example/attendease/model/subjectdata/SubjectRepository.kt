package com.example.attendease.model.subjectdata

import kotlinx.coroutines.flow.Flow


class SubjectRepository(private val subjectDao: SubjectDao) {
    val allSubjects: Flow<List<Subject>> = subjectDao.getAllSubjects()

    fun insert(subject: Subject) {
        subjectDao.addSubject(subject)
    }

     fun update(subject: Subject) {
        subjectDao.editSubject(subject)
    }

     fun delete(subject: Subject) {
        subjectDao.deleteSubject(subject)
    }

     fun present(subject: Subject){
        subjectDao.markPresent(subject.id)
    }

     fun absent(subject: Subject){
        subjectDao.markAbsent(subject.id)
    }

    fun resetAttendance(subject: Subject){
        subjectDao.resetAttendance(subject.id)
    }

    fun resetSubject(){
        subjectDao.resetSubjects()
    }

    fun presentAttendance(id: Int){
        subjectDao.markPresentAttendance(id)
    }

    fun absentAttendance(id: Int){
        subjectDao.markAbsentAttendance(id)
    }

    fun deletePresentAttendance(id: Int){
        subjectDao.deletePresentAttendance(id)
    }

    fun deleteAbsentAttendance(id: Int){
        subjectDao.deleteAbsentAttendance(id)
    }

}
