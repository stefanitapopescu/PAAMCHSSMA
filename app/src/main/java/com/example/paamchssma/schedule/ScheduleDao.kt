package com.example.paamchssma.schedule

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    @Query("SELECT * FROM schedule_entries WHERE userId = :userId ORDER BY time")
    fun observeEntriesForUser(userId: String): Flow<List<ScheduleEntry>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: ScheduleEntry)

    @Delete
    suspend fun delete(entry: ScheduleEntry)
}

