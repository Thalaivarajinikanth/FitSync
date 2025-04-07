package com.example.fitfync.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "meal_logs")
data class MealLog(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val mealType: String,
    val foodItem: String,
    val calories: String,
    val timestamp: Long = System.currentTimeMillis()
)
