package com.wesley.medcare.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wesley.medcare.data.container.AppContainer
import com.wesley.medcare.ui.model.History
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val container = AppContainer(application)
    private val repository = container.historyRepository

    private val _recentActivityList = MutableStateFlow<List<History>>(emptyList())
    val recentActivityList: StateFlow<List<History>> = _recentActivityList

    private val _compliancePercentage = MutableStateFlow(0)
    val compliancePercentage: StateFlow<Int> = _compliancePercentage

    private val _missedDosesCount = MutableStateFlow(0)
    val missedDosesCount: StateFlow<Int> = _missedDosesCount

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message

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
        // Stats total dihitung Senin - Minggu sesuai instruksi
        val response = repository.getWeeklyComplianceStatsTotal()
        _compliancePercentage.value = response?.data ?: 0
    }

    private suspend fun fetchMissedDoses() {
        // Missed dose dihitung Senin - Minggu sesuai instruksi
        val response = repository.getWeeklyMissedDose()
        _missedDosesCount.value = response?.data ?: 0
    }

    private suspend fun fetchRecentActivity() {
        val response = repository.getRecentActivity()

        val mappedList = response?.data?.map { dto ->
            History(
                id = dto.id,
                medicineName = dto.medicineName ?: "Unknown",
                scheduledDate = dto.scheduledDate ?: "",
                scheduledTime = dto.scheduledTime ?: "", // Ini kunci agar jam history = jam reminder
                status = (dto.status ?: "PENDING").uppercase(),
                timeTaken = dto.timeTaken ?: ""
            )
        } ?: emptyList()

        // Filter hanya yang sudah DONE atau MISSED
        _recentActivityList.value = mappedList.filter {
            it.status == "DONE" || it.status == "MISSED"
        }
    }
}