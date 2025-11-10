package com.example.paamchssma.schedule

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "schedule_entries")
data class ScheduleEntry(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val userId: String, // Email-ul user-ului care a creat cursul
    val time: String,
    val courseName: String,
    val faculty: String,
    val room: String,
    val floor: String
)

