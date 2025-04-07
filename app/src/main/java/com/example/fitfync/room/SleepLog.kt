package com.example.fitfync.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sleep_logs")
data class SleepLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val hours: String,
    val quality: String,
    val timestamp: Long = System.currentTimeMillis()
)
