package com.example.fitfync.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitfync.room.AppDatabase
import com.example.fitfync.room.MealLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MealViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.Companion.getDatabase(application).mealDao()

    private val _meals = MutableStateFlow<List<MealLog>>(emptyList())
    val meals: StateFlow<List<MealLog>> = _meals.asStateFlow()

    init {
        viewModelScope.launch {
            dao.getAllMeals().collect {
                _meals.value = it
            }
        }
    }

    fun insertMeal(meal: MealLog) {
        viewModelScope.launch {
            dao.insertMeal(meal)
        }
    }

    fun clearAllMeals() {
        viewModelScope.launch {
            dao.clearMeals()
        }
    }
}