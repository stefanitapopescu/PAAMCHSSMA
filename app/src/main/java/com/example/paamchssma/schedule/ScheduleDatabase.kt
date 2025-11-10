package com.example.paamchssma.schedule

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [ScheduleEntry::class],
    version = 2, // Incrementat pentru userId
    exportSchema = false
)
abstract class ScheduleDatabase : RoomDatabase() {

    abstract fun scheduleDao(): ScheduleDao

    companion object {
        @Volatile
        private var INSTANCE: ScheduleDatabase? = null

        fun getInstance(context: Context): ScheduleDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): ScheduleDatabase {
            return Room.databaseBuilder(
                context.applicationContext,
                ScheduleDatabase::class.java,
                "schedule_database"
            )
            .fallbackToDestructiveMigration() // Șterge datele vechi când se schimbă structura
            .build()
        }
    }
}

