package com.example.fitfync

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "workout_logs")
data class WorkoutLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val workoutType: String,
    val duration: String,
    val caloriesBurned: String,
    val location: String,
    val timestamp: Long = System.currentTimeMillis()
)
