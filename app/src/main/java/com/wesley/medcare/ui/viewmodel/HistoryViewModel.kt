package com.wesley.medcare.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wesley.medcare.data.container.AppContainer
import com.wesley.medcare.data.dto.History.HistoryData // The DTO from Database
import com.wesley.medcare.ui.model.History // <--- IMPORT YOUR UI MODEL
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val container = AppContainer(application)
    private val repository = container.historyRepository

    // CHANGE 1: Update the type to List<History> (your UI model)
    private val _recentActivityList = MutableStateFlow<List<History>>(emptyList())
    val recentActivityList: StateFlow<List<History>> = _recentActivityList

    // Keep other stats as they were (assuming they are just numbers or generic lists)
    private val _compliancePercentage = MutableStateFlow(0)
    val compliancePercentage: StateFlow<Int> = _compliancePercentage

    private val _missedDosesCount = MutableStateFlow(0)
    val missedDosesCount: StateFlow<Int> = _missedDosesCount

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

    fun clearMessage() { _message.value = null }

    fun refreshDashboard() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                fetchCompliance()
                fetchMissedDoses()
                fetchRecentActivity()
            } catch (e: Exception) {
                Log.e("HistoryVM", "Error refreshing dashboard", e)
                _message.value = "Failed to load history data"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private suspend fun fetchCompliance() {
        val response = repository.getWeeklyComplianceStatsTotal()
        _compliancePercentage.value = response?.data ?: 0
    }

    private suspend fun fetchMissedDoses() {
        val response = repository.getWeeklyMissedDose()
        _missedDosesCount.value = response?.data ?: 0
    }

    private suspend fun fetchRecentActivity() {
        // CHANGE 1: Call getAllHistory() instead of getRecentActivity()
        // We need the full list to calculate the chart correctly.
        val response = repository.getAllHistory()

        val mappedList = response?.data?.map { dto ->
            History(
                id = dto.id,
                medicineName = dto.medicineName ?: "Unknown",
                scheduledDate = dto.scheduledDate ?: "",
                scheduledTime = dto.scheduledTime ?: "",
                status = (dto.status ?: "PENDING").uppercase(),
                timeTaken = dto.timeTaken ?: ""
            )
        } ?: emptyList()

        // CHANGE 2: Sort by Date/Time Descending (Newest first)
        // This ensures RecentActivityCard shows the latest items,
        // while the Chart can still use the whole list.
        val sortedList = mappedList.sortedByDescending { it.scheduledDate + it.scheduledTime }

        _recentActivityList.value = sortedList
    }

    fun undoMarkAsTaken(historyId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = repository.undoMarkAsTaken(historyId)
                if (success) {
                    _message.value = "Action undone"
                    refreshDashboard()
                } else {
                    _message.value = "Failed to undo"
                }
            } catch (e: Exception) {
                _message.value = "Error: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
}