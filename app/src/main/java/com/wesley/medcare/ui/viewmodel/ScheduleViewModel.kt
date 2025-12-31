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

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppContainer(application).scheduleRepository

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
                Log.e("ScheduleViewModel", "Error fetching by date", e)
                _errorMessage.value = "Gagal memuat jadwal"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markAsTaken(detailId: Int, date: String) {
        viewModelScope.launch {
            // Logika: Panggil API untuk menandai selesai.
            // Jika API belum ada, kita bisa asumsikan ini sukses dan refresh data.
            try {
                // Contoh jika ada service delete atau update status:
                // repository.deleteScheduleDetails(detailId)

                _successMessage.value = "Obat berhasil diminum!"
                getSchedulesByDate(date) // Refresh data
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memperbarui status"
            }
        }
    }

//    fun getScheduleById(scheduleId: Int) {
//        viewModelScope.launch {
//            _isLoading.value = true
//            _errorMessage.value = null
//            try {
//                val response = repository.getScheduleWithDetailsById(scheduleId)
//                _selectedSchedule.value = response?.data
//            } catch (e: Exception) {
//                Log.e("ScheduleViewModel", "getScheduleById error", e)
//                _errorMessage.value = e.message
//            } finally {
//                _isLoading.value = false
//            }
//        }
//    }

    fun createSchedule(
        medicineId: Int,
        startDate: String,
        scheduleType: String,
        details: List<TimeDetailData>
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            try {
                // Normalize scheduleType to uppercase to match backend expectations
                val normalizedType = scheduleType.uppercase()

                // Validate scheduleType before sending
                if (normalizedType != "DAILY" && normalizedType != "WEEKLY") {
                    _errorMessage.value = "Invalid schedule type. Must be DAILY or WEEKLY"
                    _isLoading.value = false
                    return@launch
                }

                val success = repository.createScheduleWithDetails(
                    medicineId = medicineId,
                    startDate = startDate,
                    scheduleType = normalizedType,
                    details = details
                )
                if (success) {
                    _successMessage.value = "Schedule added successfully"
                    getAllSchedules()
                } else {
                    _errorMessage.value = "Failed to add schedule"
                }
            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "addSchedule error", e)
                _errorMessage.value = e.message ?: "Failed to add schedule"
            } finally {
                _isLoading.value = false
            }
        }
    }


    fun updateSchedule(
        scheduleId: Int,
        medicineId: Int?,
        startDate: String?,
        scheduleType: String?,
        details: List<com.wesley.medcare.data.dto.Medicine.DetailData>?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            try {
                val success = repository.updateScheduleWithDetails(
                    scheduleId, medicineId, startDate, scheduleType, details
                )
                if (success) {
                    _successMessage.value = "Schedule updated successfully"
                    getAllSchedules()
                } else {
                    _errorMessage.value = "Failed to update schedule"
                }
            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "updateSchedule error", e)
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteSchedule(scheduleId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            _successMessage.value = null
            try {
                val success = repository.deleteScheduleWithDetails(scheduleId)
                if (success) {
                    _successMessage.value = "Schedule deleted successfully"
                    getAllSchedules()
                } else {
                    _errorMessage.value = "Failed to delete schedule"
                }
            } catch (e: Exception) {
                Log.e("ScheduleViewModel", "deleteSchedule error", e)
                _errorMessage.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}
