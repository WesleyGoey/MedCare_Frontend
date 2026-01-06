package com.wesley.medcare.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wesley.medcare.data.alarm.AlarmScheduler
import com.wesley.medcare.data.container.AppContainer
import com.wesley.medcare.data.dto.Schedule.DetailData
import com.wesley.medcare.data.dto.Schedule.TimeDetailData
import com.wesley.medcare.ui.model.History
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val container = AppContainer(application)
    private val repository = container.scheduleRepository
    private val historyRepository = container.historyRepository
    private val alarmScheduler by lazy { AlarmScheduler(application) }

    private val _schedules = MutableStateFlow<List<DetailData>>(emptyList())
    val schedules: StateFlow<List<DetailData>> = _schedules

    private val _editingScheduleDetails = MutableStateFlow<List<DetailData>>(emptyList())
    val editingScheduleDetails: StateFlow<List<DetailData>> = _editingScheduleDetails

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

    fun getSchedulesByDate(date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getScheduleWithDetailsByDate(date)
                _schedules.value = response?.data ?: emptyList()
            } catch (e: Exception) {
                _errorMessage.value = "Gagal memuat jadwal"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun getScheduleById(scheduleId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getScheduleWithDetailsById(scheduleId)
                _editingScheduleDetails.value = response?.data ?: emptyList()
            } catch (e: Exception) {
                _errorMessage.value = "Gagal mengambil data jadwal"
            } finally {
                _isLoading.value = false
            }
        }
    }

<<<<<<< HEAD
    fun createSchedule(medicineId: Int, medicineName: String, startDate: String, details: List<TimeDetailData>) {
        if (medicineId == 0) {
            _errorMessage.value = "Silakan pilih obat terlebih dahulu"
            return
        }

=======
    // UPDATE: Menambahkan medicineName agar bisa ditampilkan di notifikasi alarm
    fun createSchedule(
        medicineId: Int,
        medicineName: String,
        startDate: String,
        details: List<TimeDetailData>
    ) {
>>>>>>> fj
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = repository.createScheduleWithDetails(medicineId, startDate, details)
                if (success) {
                    // REVISI: Jadwalkan alarm saat jadwal baru dibuat
                    details.forEachIndexed { index, detail ->
                        val uniqueId = medicineId * 100 + index
                        alarmScheduler.schedule(uniqueId, detail.time, medicineName)
                    }
                    _successMessage.value = "Jadwal berhasil disimpan"
                } else {
                    _errorMessage.value = "Gagal menyimpan jadwal ke server"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan jaringan"
            } finally {
                _isLoading.value = false
            }
        }
    }

<<<<<<< HEAD
    fun updateSchedule(scheduleId: Int, medicineId: Int, medicineName: String, startDate: String, details: List<TimeDetailData>) {
=======
    // UPDATE: Menambahkan medicineName untuk memperbarui alarm
    fun updateSchedule(
        scheduleId: Int,
        medicineId: Int,
        medicineName: String,
        startDate: String,
        details: List<TimeDetailData>
    ) {
>>>>>>> fj
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success =
                    repository.updateScheduleWithDetails(scheduleId, medicineId, startDate, details)
                if (success) {
                    // Hapus alarm lama dan buat yang baru
                    for (i in 0..5) {
                        alarmScheduler.cancel(medicineId * 100 + i)
                    }
                    details.forEachIndexed { index, detail ->
                        val uniqueId = medicineId * 100 + index
                        alarmScheduler.schedule(uniqueId, detail.time, medicineName)
                    }
                    _successMessage.value = "Jadwal berhasil diperbarui"
                } else {
                    _errorMessage.value = "Gagal memperbarui jadwal"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan saat memperbarui jadwal"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun deleteSchedule(scheduleId: Int, medicineId: Int, dateToRefresh: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (repository.deleteScheduleWithDetails(scheduleId)) {
                    for (i in 0..5) {
                        alarmScheduler.cancel(medicineId * 100 + i)
                    }
                    _successMessage.value = "Jadwal berhasil dihapus"
                    getSchedulesByDate(dateToRefresh)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal menghapus jadwal"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun markAsTaken(detailId: Int, originalScheduledDate: String) {
        viewModelScope.launch {
            // Prepare the date string FIRST
            val dateToSend = if (originalScheduledDate.contains("T")) {
                originalScheduledDate
            } else {
                "${originalScheduledDate}T00:00:00"
            }

            // FIX: Pass 'dateToSend' to updateLocalStatus
            updateLocalStatus(detailId, "DONE", dateToSend)

            try {
                val jakartaZone = ZoneId.of("Asia/Jakarta")
                val timeTakenNow = LocalTime.now(jakartaZone).format(DateTimeFormatter.ofPattern("HH:mm"))

                val success = historyRepository.markAsTaken(detailId, dateToSend, timeTakenNow)

                // FIX: Pass 'dateToSend' here too if we need to revert
                if (!success) updateLocalStatus(detailId, "PENDING", dateToSend)

            } catch (e: Exception) {
                updateLocalStatus(detailId, "PENDING", dateToSend)
            }
        }
    }

    // 2. Undo Mark As Taken (CRITICAL FIX HERE)
    fun undoMarkAsTaken(detailId: Int, viewedDate: String) {
        viewModelScope.launch {
            // Prepare the date string FIRST
            val dateToSend = if (viewedDate.contains("T")) {
                viewedDate
            } else {
                "${viewedDate}T00:00:00"
            }

            // FIX: Pass 'dateToSend' to updateLocalStatus
            updateLocalStatus(detailId, "PENDING", dateToSend)

            try {
                val success = historyRepository.undoMarkAsTaken(detailId, dateToSend)

                if (!success) {
                    // Revert to DONE if failed
                    updateLocalStatus(detailId, "DONE", dateToSend)
                    _errorMessage.value = "Failed to undo"
                }
            } catch (e: Exception) {
                updateLocalStatus(detailId, "DONE", dateToSend)
                _errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    // 3. Helper for Optimistic Updates
    private fun updateLocalStatus(scheduleId: Int, newStatus: String, currentDateString: String) {
        val currentList = _schedules.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == scheduleId }

        if (index != -1) {
            val item = currentList[index]

            // Create the fake history item for immediate UI feedback
            val newHistoryItem = History(
                status = newStatus,
                // FIX: Use the passed date string instead of 'item.scheduledDate'
                scheduledDate = currentDateString,
                id = 0,
                scheduledTime = item.time 
            )

            val updatedHistoryList = listOf(newHistoryItem)
            currentList[index] = item.copy(history = updatedHistoryList)
            _schedules.value = ArrayList(currentList)
        }
    }
}