package com.example.attendease.timetabledata


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
}
