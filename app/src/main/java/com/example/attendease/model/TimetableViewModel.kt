package com.example.attendease.model

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendease.subjectdata.Subject
import com.example.attendease.timetabledata.Timetable
import com.example.attendease.timetabledata.TimetableRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

class TimetableViewModel(private val repository: TimetableRepository) : ViewModel() {

    private val currentDay = LocalDate.now().dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH).uppercase()

    private val dayMap = mapOf(
        "MON" to "MONDAY",
        "TUE" to "TUESDAY",
        "WED" to "WEDNESDAY",
        "THU" to "THURSDAY",
        "FRI" to "FRIDAY",
        "SAT" to "SATURDAY",
        "SUN" to "SUNDAY"
    )

    fun getFullDay(shortDay: String): String {
        return dayMap[shortDay] ?: shortDay // Default to shortDay if no match found
    }

    private var _selectedDay = mutableStateOf(currentDay)

    private val _classesForDate = MutableStateFlow<List<Timetable>>(emptyList())
    val classesForDate: StateFlow<List<Timetable>> = _classesForDate

    init {
        loadClasses(_selectedDay.value)
    }

    fun setSelectedDay(day: String) {
        _selectedDay.value = day
        loadClasses(day)
    }

    fun loadClasses(day: String = _selectedDay.value) {
        viewModelScope.launch(Dispatchers.IO) {
            _classesForDate.value = repository.getClassesForDate(day)
        }
    }

    fun addClass(name: String, day: String, startTime: String, endTime: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = Timetable(subjectName = name, day = day, startTime = startTime, endTime = endTime)
            repository.addClass(item)
            if (item.day == _selectedDay.value) {
                loadClasses(day) // only reload if it matches selected
            }
        }
    }

    fun deleteClass(item: Timetable) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteClass(item)
            if (item.day == _selectedDay.value) {
                loadClasses(item.day)
            }
        }
    }

    fun updateClass(id: Int, className:String,day: String,startTime: String,endTime: String) {
        viewModelScope.launch(Dispatchers.IO) {
            val item = Timetable(Id=id, subjectName = className, day = day, startTime = startTime, endTime = endTime)
            repository.updateClass(item)
            if (item.day == _selectedDay.value) {
                loadClasses(item.day)
            }
        }
    }

    fun resetTimetable(){
        viewModelScope.launch(Dispatchers.IO){
            repository.resetTimetable()
            _classesForDate.value = repository.getClassesForDate(_selectedDay.value)
        }
    }

    fun deleteClassBasedOnSubject(subject: Subject){
        viewModelScope.launch(Dispatchers.IO){
            repository.deleteClassBasedOnSubject(subject)
            _classesForDate.value = repository.getClassesForDate(_selectedDay.value)
        }
    }
}
