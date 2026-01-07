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
import kotlinx.coroutines.flow.update
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

    private var currentActiveDate: String? = null

    fun clearMessages() {
        _errorMessage.value = null
        _successMessage.value = null
    }

    // --- READ FUNCTIONS ---

    fun getSchedulesByDate(date: String, forceRefresh: Boolean = false) {
        // PERBAIKAN: Jika tanggal sama dan sudah ada data, jangan fetch lagi agar status lokal tidak hilang
        if (currentActiveDate == date && _schedules.value.isNotEmpty() && !forceRefresh) {
            return
        }

        currentActiveDate = date
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

    // --- HISTORY LOGIC (TAKEN / UNDO) ---

    fun markAsTaken(detailId: Int, originalScheduledDate: String) {
        if (_processingIds.value.contains(detailId)) return

        viewModelScope.launch {
            _processingIds.value += detailId
            val datePart = originalScheduledDate.split("T")[0]
            val dateTimeToSend = "${datePart}T00:00:00"

            // 1. UPDATE LOKAL (UI berubah seketika)
            updateStatusLocally(detailId, "DONE", dateTimeToSend)

            try {
                val jakartaZone = ZoneId.of("Asia/Jakarta")
                val timeTakenNow = LocalTime.now(jakartaZone).format(DateTimeFormatter.ofPattern("HH:mm"))

                val success = historyRepository.markAsTaken(detailId, dateTimeToSend, timeTakenNow)

                if (success) {
                    _successMessage.value = "Berhasil mencatat obat"
                } else {
                    updateStatusLocally(detailId, "PENDING", dateTimeToSend)
                    _errorMessage.value = "Hanya bisa mencatat untuk hari ini"
                }
            } catch (e: Exception) {
                updateStatusLocally(detailId, "PENDING", dateTimeToSend)
                _errorMessage.value = "Terjadi kesalahan jaringan"
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

            // 1. UPDATE LOKAL (Kembali ke Pending)
            updateStatusLocally(detailId, "PENDING", dateTimeToSend)

            try {
                val success = historyRepository.undoMarkAsTaken(detailId, dateTimeToSend)
                if (success) {
                    _successMessage.value = "Status berhasil dibatalkan"
                } else {
                    updateStatusLocally(detailId, "DONE", dateTimeToSend)
                    _errorMessage.value = "Hanya bisa mengubah hari ini"
                }
            } catch (e: Exception) {
                updateStatusLocally(detailId, "DONE", dateTimeToSend)
                _errorMessage.value = "Kesalahan jaringan"
            } finally {
                _processingIds.value -= detailId
            }
        }
    }

    private fun updateStatusLocally(id: Int, newStatus: String, dateStr: String) {
        _schedules.update { currentList ->
            currentList.map { item ->
                if (item.id == id) {
                    val newHistory = History(
                        id = item.history?.firstOrNull()?.id ?: 0,
                        status = newStatus,
                        scheduledDate = dateStr,
                        scheduledTime = item.time,
                        timeTaken = if (newStatus == "DONE") "Now" else ""
                    )
                    item.copy(history = listOf(newHistory))
                } else {
                    item
                }
            }
        }
    }

    // --- WRITE FUNCTIONS ---

    fun createSchedule(medicineId: Int, medicineName: String, startDate: String, details: List<TimeDetailData>) {
        if (medicineId == 0) { _errorMessage.value = "Pilih obat"; return }
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = repository.createScheduleWithDetails(medicineId, startDate, details)
                if (success) {
                    details.forEachIndexed { index, d ->
                        alarmScheduler.schedule(medicineId * 100 + index, d.time, medicineName)
                    }
                    _successMessage.value = "Jadwal berhasil disimpan"
                }
            } catch (e: Exception) { _errorMessage.value = "Koneksi bermasalah" }
            finally { _isLoading.value = false }
        }
    }

    fun deleteSchedule(scheduleId: Int, medicineId: Int, dateToRefresh: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                if (repository.deleteScheduleWithDetails(scheduleId)) {
                    for (i in 0..5) alarmScheduler.cancel(medicineId * 100 + i)
                    getSchedulesByDate(dateToRefresh, forceRefresh = true)
                }
            } finally { _isLoading.value = false }
        }
    }

    fun getScheduleById(scheduleId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getScheduleWithDetailsById(scheduleId)
                _editingScheduleDetails.value = response?.data ?: emptyList()
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
                    details.forEachIndexed { index, d ->
                        alarmScheduler.schedule(medicineId * 100 + index, d.time, medicineName)
                    }
                    _successMessage.value = "Berhasil diperbarui"
                }
            } finally { _isLoading.value = false }
        }
    }
}