package com.example.attendease.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.attendease.attendancedata.Attendance
import com.example.attendease.attendancedata.AttendanceRepository
import com.example.attendease.subjectdata.Subject
import com.example.attendease.subjectdata.SubjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class DetailViewModel(private val repository: AttendanceRepository,private  val subjectRepository: SubjectRepository) : ViewModel() {

    private val _attendanceRecords = MutableStateFlow<List<Attendance>>(emptyList())
    val attendanceRecords = _attendanceRecords.asStateFlow()


    fun insertAttendanceRecord(subjectId: Int, status: String) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertAttendanceRecord(subjectId, status)
        }
    }

    fun resetAttendance(subject: Subject) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.resetAttendance(subject.id)  // Updating subject and resetting attendance
        }
    }

    fun getAttendanceRecords(subjectId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.getAttendanceRecords(subjectId)
                .collect { records ->
                    _attendanceRecords.value = records
                }
        }
    }

    fun deleteDetail(attendId: Int,subjectId: Int,status: String){
        viewModelScope.launch (Dispatchers.IO){
            repository.deleteDetail(attendId)
            if(status == "Present"){
                subjectRepository.deletePresentAttendance(subjectId)
            }else{
                subjectRepository.deleteAbsentAttendance(subjectId)
            }
        }
    }

    fun updateDetail(subjectId: Int,attendId: Int, status: String){
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateDetail(attendId,status)
            if (status == "Present") {
                subjectRepository.presentAttendance(subjectId)
            } else {
                subjectRepository.absentAttendance(subjectId)
            }
        }
    }

}

