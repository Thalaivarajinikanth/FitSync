package com.example.fitfync.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMeal(meal: MealLog)

    @Query("SELECT * FROM meal_logs ORDER BY timestamp DESC")
    fun getAllMeals(): Flow<List<MealLog>>

    @Delete
    suspend fun deleteMeal(meal: MealLog)

    @Query("DELETE FROM meal_logs")
    suspend fun clearMeals()
}
