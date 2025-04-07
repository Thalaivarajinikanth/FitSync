package com.example.fitfync.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.fitfync.room.AppDatabase
import com.example.fitfync.room.SleepLog
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SleepViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = AppDatabase.Companion.getDatabase(application).sleepDao()

    private val _sleeps = MutableStateFlow<List<SleepLog>>(emptyList())
    val sleeps: StateFlow<List<SleepLog>> = _sleeps.asStateFlow()

    init {
        viewModelScope.launch {
            dao.getAllSleeps().collect {
                _sleeps.value = it
            }
        }
    }

    fun insertSleep(log: SleepLog) {
        viewModelScope.launch {
            dao.insertSleep(log)
        }
    }

    fun clearAllSleeps() {
        viewModelScope.launch {
            dao.clearSleeps()
        }
    }
}