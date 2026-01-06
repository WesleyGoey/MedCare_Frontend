package com.wesley.medcare.ui.viewmodel

import android.app.Application
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

    private val _processingIds = MutableStateFlow<Set<Int>>(emptySet())
    val processingIds: StateFlow<Set<Int>> = _processingIds

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    // --- READ FUNCTIONS ---

    fun getSchedulesByDate(date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getScheduleWithDetailsByDate(date)
                response?.data?.let { _schedules.value = it }
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

    // --- WRITE FUNCTIONS ---

    fun createSchedule(medicineId: Int, medicineName: String, startDate: String, details: List<TimeDetailData>) {
        if (medicineId == 0) {
            _errorMessage.value = "Silakan pilih obat terlebih dahulu"
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = repository.createScheduleWithDetails(medicineId, startDate, details)
                if (success) {
                    details.forEachIndexed { index, detail ->
                        val uniqueId = medicineId * 100 + index
                        alarmScheduler.schedule(uniqueId, detail.time, medicineName)
                    }
                    _successMessage.value = "Jadwal & Alarm berhasil dibuat"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan jaringan"
            } finally { _isLoading.value = false }
        }
    }

    fun updateSchedule(scheduleId: Int, medicineId: Int, medicineName: String, startDate: String, details: List<TimeDetailData>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = repository.updateScheduleWithDetails(scheduleId, medicineId, startDate, details)
                if (success) {
                    for (i in 0..5) alarmScheduler.cancel(medicineId * 100 + i)
                    details.forEachIndexed { index, detail ->
                        val uniqueId = medicineId * 100 + index
                        alarmScheduler.schedule(uniqueId, detail.time, medicineName)
                    }
                    _successMessage.value = "Jadwal & Alarm berhasil diperbarui"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Kesalahan saat memperbarui"
            } finally { _isLoading.value = false }
        }
    }

    fun deleteSchedule(scheduleId: Int, medicineId: Int, dateToRefresh: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (repository.deleteScheduleWithDetails(scheduleId)) {
                    for (i in 0..5) alarmScheduler.cancel(medicineId * 100 + i)
                    _successMessage.value = "Jadwal berhasil dihapus"
                    getSchedulesByDate(dateToRefresh)
                }
            } catch (e: Exception) {
                _errorMessage.value = "Gagal menghapus jadwal"
            } finally { _isLoading.value = false }
        }
    }

    // --- HISTORY LOGIC (TAKEN / UNDO) ---

    fun markAsTaken(detailId: Int, originalScheduledDate: String) {
        if (_processingIds.value.contains(detailId)) return

        viewModelScope.launch {
            _processingIds.value += detailId
            val datePart = originalScheduledDate.split("T")[0]
            val dateTimeToSend = "${datePart}T00:00:00"

            // 1. Update UI secara Instan (Optimistic Update)
            updateStatusLocally(detailId, "DONE", dateTimeToSend)

            try {
                val jakartaZone = ZoneId.of("Asia/Jakarta")
                val timeTakenNow = LocalTime.now(jakartaZone).format(DateTimeFormatter.ofPattern("HH:mm"))

                // 2. Simpan ke Backend
                val success = historyRepository.markAsTaken(detailId, dateTimeToSend, timeTakenNow)

                if (success) {
                    _successMessage.value = "Berhasil mencatat obat"
                } else {
                    // Revert jika gagal
                    updateStatusLocally(detailId, "PENDING", dateTimeToSend)
                    _errorMessage.value = "Gagal mencatat ke server"
                }
            } catch (e: Exception) {
                updateStatusLocally(detailId, "PENDING", dateTimeToSend)
                _errorMessage.value = "Error koneksi"
            } finally {
                _processingIds.value -= detailId
            }
        }
    }

    fun undoMarkAsTaken(detailId: Int, viewedDate: String) {
        if (_processingIds.value.contains(detailId)) return

        viewModelScope.launch {
            _processingIds.value += detailId
            val datePart = viewedDate.split("T")[0]
            val dateTimeToSend = "${datePart}T00:00:00"

            // 1. Update UI secara Instan (Optimistic Update)
            updateStatusLocally(detailId, "PENDING", dateTimeToSend)

            try {
                val success = historyRepository.undoMarkAsTaken(detailId, dateTimeToSend)

                if (success) {
                    _successMessage.value = "Berhasil membatalkan status"
                } else {
                    // Revert jika gagal
                    updateStatusLocally(detailId, "DONE", dateTimeToSend)
                    _errorMessage.value = "Gagal membatalkan status"
                }
            } catch (e: Exception) {
                updateStatusLocally(detailId, "DONE", dateTimeToSend)
                _errorMessage.value = "Error koneksi"
            } finally {
                _processingIds.value -= detailId
            }
        }
    }

    private fun updateStatusLocally(id: Int, newStatus: String, dateStr: String) {
        val currentList = _schedules.value.toMutableList()
        val index = currentList.indexOfFirst { it.id == id }

        if (index != -1) {
            val item = currentList[index]
            val fakeHistory = History(
                id = 0,
                status = newStatus,
                scheduledDate = dateStr,
                scheduledTime = item.time,
                timeTaken = if (newStatus == "DONE") "Now" else ""
            )

            currentList[index] = item.copy(history = listOf(fakeHistory))
            _schedules.value = currentList.toList()
        }
    }
}