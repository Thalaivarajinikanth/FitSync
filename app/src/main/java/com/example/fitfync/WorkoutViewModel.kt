package com.example.fitfync.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitfync.WorkoutLog
import com.example.fitfync.room.AppDatabase

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WorkoutViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.getDatabase(application).workoutDao()

    private val _workouts = MutableStateFlow<List<WorkoutLog>>(emptyList())
    val workouts: StateFlow<List<WorkoutLog>> = _workouts.asStateFlow()

    init {
        viewModelScope.launch {
            dao.getAllWorkouts().collect {
                _workouts.value = it
            }
        }
    }

    fun insertWorkout(log: WorkoutLog) {
        viewModelScope.launch {
            dao.insertWorkout(log)
        }
    }

    fun clearAllWorkouts() {
        viewModelScope.launch {
            dao.clearAll()
        }
    }
}
