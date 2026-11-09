package com.example.app.data

import androidx.room.Entity
import androidx.room.PrimaryKey

object Status {
    const val DONE = "DONE"
    const val SKIPPED = "SKIPPED"
}

@Entity(tableName = "day_records")
data class DayRecord(
    @PrimaryKey val epochDay: Long,
    val status: String
)
@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val text: String,
    val updatedAt: Long
)
