package com.example.app.data

import android.content.Context
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.Room

@Database(entities = [DayRecord::class, Note::class], version = 1,
    exportSchema = false)

abstract class AppDatabase : RoomDatabase() {
    abstract fun dayRecordDao(): DayRecordDao
    abstract fun noteDao(): NoteDao
    companion object {
        @Volatile private var instance: AppDatabase? = null
        fun get(context: Context): AppDatabase = instance ?:
        synchronized(this) {
            instance ?: Room.databaseBuilder(
                context.applicationContext, AppDatabase::class.java,
                "selfcare.db"
            ).build().also { instance = it }
        }
    }
}