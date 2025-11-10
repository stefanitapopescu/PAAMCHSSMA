package com.example.paamchssma.schedule

class ScheduleRepository(private val dao: ScheduleDao) {

    fun getEntriesForUser(userId: String) = dao.observeEntriesForUser(userId)

    suspend fun addEntry(entry: ScheduleEntry) {
        dao.insert(entry)
    }

    suspend fun deleteEntry(entry: ScheduleEntry) {
        dao.delete(entry)
    }
}

