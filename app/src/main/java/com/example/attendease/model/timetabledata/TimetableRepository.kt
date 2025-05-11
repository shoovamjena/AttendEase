package com.example.attendease.model.timetabledata

import com.example.attendease.model.subjectdata.Subject
import com.example.attendease.model.subjectdata.SubjectDao


class TimetableRepository(private val dao: TimetableDao) {

    fun getClassesForDate(date: String): List<Timetable> {
        return dao.getClassesByDate(date)
    }

    fun addClass(item: Timetable) {
        dao.insertClass(item)
    }

    fun deleteClass(item: Timetable) {
        dao.deleteClass(item)
    }

    fun updateClass(item: Timetable) {
        dao.updateClass(item)
    }

    fun resetTimetable(){
        dao.resetTimetable()
    }

    fun deleteClassBasedOnSubject(subject: Subject){
        dao.deleteClassBasedOnSubject(subject.name)
    }
}
