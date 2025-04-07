package com.example.fitfync.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.fitfync.WorkoutLog
import com.example.fitfync.room.MealLog


@Database(
    entities = [WorkoutLog::class, MealLog::class],
    version = 2, // Version bumped
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun workoutDao(): WorkoutDao
    abstract fun mealDao(): MealDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "fitfync_database"
                )
                    .fallbackToDestructiveMigration() //  Handles schema changes during dev
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
