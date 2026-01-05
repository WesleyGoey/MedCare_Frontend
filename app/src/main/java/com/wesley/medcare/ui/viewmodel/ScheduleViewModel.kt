package com.wesley.medcare.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wesley.medcare.data.container.AppContainer
import com.wesley.medcare.data.dto.Schedule.DetailData
import com.wesley.medcare.data.dto.Schedule.TimeDetailData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppContainer(application).scheduleRepository
    private val historyRepository = AppContainer(application).historyRepository
    private val _schedules = MutableStateFlow<List<DetailData>>(emptyList())
    val schedules: StateFlow<List<DetailData>> = _schedules

    private val _selectedSchedule = MutableStateFlow<DetailData?>(null)
    val selectedSchedule: StateFlow<DetailData?> = _selectedSchedule

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage: StateFlow<String?> = _successMessage

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    fun getAllSchedules() {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = repository.getAllScheduleWithDetails()
                _schedules.value = response?.data ?: emptyList()
            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "getAllSchedules error", e)
                _errorMessage.value = e.message ?: "Failed to load schedules"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun getSchedulesByDate(date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                val response = repository.getScheduleWithDetailsByDate(date)
                _schedules.value = response?.data ?: emptyList()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to load schedules"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markAsTaken(detailId: Int, date: String) {
        viewModelScope.launch {
            _isLoading.value = true

            // Mengambil waktu saat ini (LocalTime) untuk timeTaken
            val currentTime = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

            val success = historyRepository.markAsTaken(
                detailId = detailId,
                date = date,
                timeTaken = currentTime
            )

            if (success) {
                _successMessage.value = "Medicine recorded successfully!"
                getSchedulesByDate(date) // Refresh list
            } else {
                _errorMessage.value = "Failed to record medicine"
            }
            _isLoading.value = false
        }
    }

    private val _editingScheduleDetails = MutableStateFlow<List<DetailData>>(emptyList())
    val editingScheduleDetails: StateFlow<List<DetailData>> = _editingScheduleDetails

    fun getScheduleById(scheduleId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getScheduleWithDetailsById(scheduleId)
                // Backend mengembalikan List<DetailData> untuk 1 scheduleId
                _editingScheduleDetails.value = response?.data ?: emptyList()
            } catch (e: Exception) {
                _errorMessage.value = "Failed to fetch schedule data"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createSchedule(
        medicineId: Int,
        startDate: String,
        details: List<TimeDetailData>
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Kita tidak perlu lagi kirim scheduleType karena backend sudah pasti DAILY
                val success = repository.createScheduleWithDetails(
                    medicineId = medicineId,
                    startDate = startDate,
                    scheduleType = "DAILY", // Kirim default atau hapus param di repository
                    details = details
                )
                if (success) {
                    _successMessage.value = "Schedule added successfully"
                    getAllSchedules()
                } else {
                    _errorMessage.value = "Failed to add schedule"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Failed to add schedule"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun updateSchedule(
        scheduleId: Int,
        medicineId: Int,
        startDate: String,
        details: List<TimeDetailData>
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            try {
                // Panggil repository tanpa scheduleType
                val success = repository.updateScheduleWithDetails(
                    scheduleId = scheduleId,
                    medicineId = medicineId,
                    startDate = startDate,
                    details = details
                )
                if (success) {
                    _successMessage.value = "Schedule updated successfully"
                    getAllSchedules() // Refresh list utama
                } else {
                    _errorMessage.value = "Failed to update schedule"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }


    // Fungsi Delete (Soft Delete di Backend)
    fun deleteSchedule(scheduleId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.deleteScheduleWithDetails(scheduleId)
            if (success) {
                _successMessage.value = "Schedule deleted successfully"
                // Refresh data setelah delete
                getAllSchedules()
            } else {
                _errorMessage.value = "Failed to delete schedule"
            }
            _isLoading.value = false
        }
    }

    fun validateAndCreateSchedule(medicineId: Int, startDate: String, details: List<TimeDetailData>) {
        if (details.size > 3) {
            _errorMessage.value = "Maximum 3 reminder times allowed"
            return
        }
        createSchedule(medicineId, startDate, details)
    }
}

