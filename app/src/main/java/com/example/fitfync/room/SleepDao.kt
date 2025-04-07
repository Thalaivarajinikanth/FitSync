package com.example.fitfync.room

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface SleepDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSleep(log: SleepLog)

    @Query("SELECT * FROM sleep_logs ORDER BY timestamp DESC")
    fun getAllSleeps(): Flow<List<SleepLog>>

    @Delete
    suspend fun deleteSleep(log: SleepLog)

    @Query("DELETE FROM sleep_logs")
    suspend fun clearSleeps()
}
