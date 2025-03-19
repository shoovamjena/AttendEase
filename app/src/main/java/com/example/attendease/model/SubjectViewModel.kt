package com.example.attendease.model

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.attendease.subjectdata.Subject
import com.example.attendease.subjectdata.SubjectRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class SubjectViewModel(private val repository: SubjectRepository) : ViewModel() {

    private val _subjects = MutableStateFlow<List<Subject>>(emptyList())
    val subjects = _subjects.asStateFlow()

    init {
        loadSubjects()
    }

    private fun loadSubjects() {
        viewModelScope.launch(Dispatchers.IO) {
            repository.allSubjects.collect { subjectsList ->
                _subjects.value = subjectsList
            }
        }
    }

    fun addSubject(name: String,attend: Int, total: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val subject = Subject(name = name, attend = attend, total = total)
            repository.insert(subject)
        }
    }

    fun deleteSubject(subject: Subject) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.delete(subject)
        }
    }

    fun markPresent(subject: Subject) {
        viewModelScope.launch(Dispatchers.IO) { repository.present(subject) }
    }

    fun markAbsent(subject: Subject) {
        viewModelScope.launch(Dispatchers.IO) { repository.absent(subject) }
    }

    fun resetAttendance(subject: Subject){
        viewModelScope.launch(Dispatchers.IO){ repository.resetAttendance(subject) }
    }

    fun updateSubject(id: Int,newName: String, newAttend: Int, newTotal: Int){
        viewModelScope.launch(Dispatchers.IO) {
            val subject = Subject(id = id, name = newName, attend = newAttend, total = newTotal)
            repository.update(subject)
        }
    }
}

class SubjectViewModelFactory(private var repository: SubjectRepository,application: Application): ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if(modelClass.isAssignableFrom(SubjectViewModel :: class.java)){
            return SubjectViewModel(repository) as T
        }else{
            throw IllegalArgumentException("unknown View Model")
        }
    }
}