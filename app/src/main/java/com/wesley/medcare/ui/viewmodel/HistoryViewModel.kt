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
        val response = repository.getRecentActivity()

        // CHANGE 2: Map the DTO (HistoryData) to your UI Model (History)
        val mappedList = response?.data?.map { dto ->
            History(
                id = dto.id,
                medicineName = dto.medicineName ?: "Unknown",

                // FIX: Use the specific property names found in your DTO class
                scheduledDate = dto.scheduledDate ?: "",
                scheduledTime = dto.scheduledTime ?: "",

                status = dto.status ?: "PENDING",
                timeTaken = dto.timeTaken ?: ""
            )
        } ?: emptyList()

        _recentActivityList.value = mappedList
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

//package com.wesley.medcare.ui.viewmodel
//
//import android.app.Application
//import android.util.Log
//import androidx.lifecycle.AndroidViewModel
//import androidx.lifecycle.viewModelScope
//import com.wesley.medcare.data.container.AppContainer
//import com.wesley.medcare.data.dto.History.HistoryData
//import kotlinx.coroutines.flow.MutableStateFlow
//import kotlinx.coroutines.flow.StateFlow
//import kotlinx.coroutines.launch
//
//class HistoryViewModel(application: Application) : AndroidViewModel(application) {
//
//    private val repository = AppContainer(application).historyRepository
//
//
//    private val _historyList = MutableStateFlow<List<HistoryData>>(emptyList())
//    val historyList: StateFlow<List<HistoryData>> = _historyList
//
//    private val _recentActivityList = MutableStateFlow<List<HistoryData>>(emptyList())
//    val recentActivityList: StateFlow<List<HistoryData>> = _recentActivityList
//
//    private val _compliancePercentage = MutableStateFlow(0)
//    val compliancePercentage: StateFlow<Int> = _compliancePercentage
//
//    private val _missedDosesCount = MutableStateFlow(0)
//    val missedDosesCount: StateFlow<Int> = _missedDosesCount
//
//    private val _weeklyStats = MutableStateFlow<List<HistoryData>>(emptyList())
//    val weeklyStats: StateFlow<List<HistoryData>> = _weeklyStats
//
//    private val _isLoading = MutableStateFlow(false)
//    val isLoading: StateFlow<Boolean> = _isLoading
//
//    private val _errorMessage = MutableStateFlow<String?>(null)
//    val errorMessage: StateFlow<String?> = _errorMessage
//
//    private val _successMessage = MutableStateFlow<String?>(null)
//    val successMessage: StateFlow<String?> = _successMessage
//
//
//    fun clearMessages() {
//        _errorMessage.value = null
//        _successMessage.value = null
//    }
//
//    fun refreshDashboard() {
//        getAllHistory()
//        getComplianceTotal()
//        getWeeklyMissedDoses()
//        getWeeklyStats()
//        getRecentActivity()
//    }
//
//    fun getAllHistory() {
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                val resp = repository.getAllHistory()
//                _historyList.value = resp?.data ?: emptyList()
//            } catch (e: Exception) {
//                Log.e("HistoryViewModel", "getAllHistory error", e)
//                _errorMessage.value = "Failed to load history"
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }
//
//    fun getRecentActivity() {
//        viewModelScope.launch {
//            try {
//                val resp = repository.getRecentActivity()
//                _recentActivityList.value = resp?.data ?: emptyList()
//            } catch (e: Exception) {
//                Log.e("HistoryViewModel", "getRecentActivity error", e)
//            }
//        }
//    }
//
//    fun getComplianceTotal() {
//        viewModelScope.launch {
//            try {
//                val resp = repository.getWeeklyComplianceStatsTotal()
//                _compliancePercentage.value = resp?.data ?: 0
//            } catch (e: Exception) {
//                Log.e("HistoryViewModel", "getComplianceTotal error", e)
//            }
//        }
//    }
//
//    fun getWeeklyMissedDoses() {
//        viewModelScope.launch {
//            try {
//                val resp = repository.getWeeklyMissedDose()
//                _missedDosesCount.value = resp?.data ?: 0
//            } catch (e: Exception) {
//                Log.e("HistoryViewModel", "getWeeklyMissedDoses error", e)
//            }
//        }
//    }
//
//    fun getWeeklyStats() {
//        viewModelScope.launch {
//            try {
//                val resp = repository.getWeeklyComplianceStats()
//                _weeklyStats.value = resp?.data ?: emptyList()
//            } catch (e: Exception) {
//                Log.e("HistoryViewModel", "getWeeklyStats error", e)
//            }
//        }
//    }
//
//    fun markAsTaken(id: Int, date: String, timeTaken: String) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            _errorMessage.value = null
//            try {
//                val success = repository.markAsTaken(id, date, timeTaken)
//
//                if (success) {
//                    _successMessage.value = "Medicine marked as taken"
//                    refreshDashboard()
//                } else {
//                    _errorMessage.value = "Failed to update status"
//                }
//            } catch (e: Exception) {
//                Log.e("HistoryViewModel", "markAsTaken error", e)
//                _errorMessage.value = e.message
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }
//
//    fun skipOccurrence(id: Int, date: String) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                val success = repository.skipOccurrence(id, date)
//
//                if (success) {
//                    _successMessage.value = "Medicine skipped"
//                    refreshDashboard()
//                } else {
//                    _errorMessage.value = "Failed to skip medicine"
//                }
//            } catch (e: Exception) {
//                Log.e("HistoryViewModel", "skipOccurrence error", e)
//                _errorMessage.value = e.message
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }
//
//    fun undoMarkAsTaken(id: Int, date: String) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            try {
//                val success = repository.undoMarkAsTaken(id, date)
//
//                if (success) {
//                    _successMessage.value = "Status reverted"
//                    refreshDashboard()
//                } else {
//                    _errorMessage.value = "Failed to undo status"
//                }
//            } catch (e: Exception) {
//                Log.e("HistoryViewModel", "undoMarkAsTaken error", e)
//                _errorMessage.value = e.message
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }
//}