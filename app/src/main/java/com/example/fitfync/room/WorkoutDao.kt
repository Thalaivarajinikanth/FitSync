package com.example.fitfync.room

import androidx.room.*
import com.example.fitfync.WorkoutLog
import kotlinx.coroutines.flow.Flow

@Dao
interface WorkoutDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWorkout(workout: WorkoutLog)

    @Query("SELECT * FROM workout_logs ORDER BY timestamp DESC")
    fun getAllWorkouts(): Flow<List<WorkoutLog>>

    @Delete
    suspend fun deleteWorkout(workout: WorkoutLog)

    @Query("DELETE FROM workout_logs")
    suspend fun clearAll()
}
