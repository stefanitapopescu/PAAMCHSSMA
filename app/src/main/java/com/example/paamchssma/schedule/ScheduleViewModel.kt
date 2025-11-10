package com.example.paamchssma.schedule

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ScheduleViewModel(
    private val repository: ScheduleRepository,
    private val userId: String // Email-ul user-ului curent
) : ViewModel() {

    private val _entries = MutableStateFlow<List<ScheduleEntry>>(emptyList())
    val entries: StateFlow<List<ScheduleEntry>> = _entries.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getEntriesForUser(userId).collect { list ->
                _entries.value = list
            }
        }
    }

    fun addEntry(time: String, course: String, faculty: String, room: String, floor: String) {
        val entry = ScheduleEntry(
            userId = userId, // Asociază cursul cu user-ul curent
            time = time,
            courseName = course,
            faculty = faculty,
            room = room,
            floor = floor
        )
        viewModelScope.launch {
            repository.addEntry(entry)
        }
    }

    fun deleteEntry(entry: ScheduleEntry) {
        viewModelScope.launch {
            repository.deleteEntry(entry)
        }
    }
}

