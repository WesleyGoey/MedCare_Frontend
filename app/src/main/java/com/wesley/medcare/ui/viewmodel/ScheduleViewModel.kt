package com.wesley.medcare.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wesley.medcare.data.alarm.AlarmScheduler
import com.wesley.medcare.data.container.AppContainer
import com.wesley.medcare.data.dto.Schedule.DetailData
import com.wesley.medcare.data.dto.Schedule.TimeDetailData
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val container = AppContainer(application)
    private val repository = container.scheduleRepository
    private val historyRepository = container.historyRepository

    // TAMBAHAN: Inisialisasi AlarmScheduler
    private val alarmScheduler = AlarmScheduler(application)

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

    // UPDATE: Menambahkan medicineName agar bisa ditampilkan di notifikasi alarm
    fun createSchedule(medicineId: Int, medicineName: String, startDate: String, details: List<TimeDetailData>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = repository.createScheduleWithDetails(medicineId, startDate, details)
                if (success) {
                    // JADWALKAN ALARM
                    details.forEachIndexed { index, detail ->
                        // Gunakan ID unik: kombinasi medicineId dan index jam
                        val uniqueId = medicineId * 100 + index
                        alarmScheduler.schedule(uniqueId, detail.time, medicineName)
                    }
                    _successMessage.value = "Jadwal & Alarm berhasil dibuat"
                } else {
                    _errorMessage.value = "Gagal membuat jadwal"
                }
            } catch (e: Exception) {
                _errorMessage.value = "Terjadi kesalahan saat membuat jadwal"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // UPDATE: Menambahkan medicineName untuk memperbarui alarm
    fun updateSchedule(scheduleId: Int, medicineId: Int, medicineName: String, startDate: String, details: List<TimeDetailData>) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val success = repository.updateScheduleWithDetails(scheduleId, medicineId, startDate, details)
                if (success) {
                    // Batalkan alarm lama dan pasang yang baru
                    details.forEachIndexed { index, detail ->
                        val uniqueId = medicineId * 100 + index
                        alarmScheduler.cancel(uniqueId)
                        alarmScheduler.schedule(uniqueId, detail.time, medicineName)
                    }
                    _successMessage.value = "Jadwal & Alarm berhasil diperbarui"
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
            if (repository.deleteScheduleWithDetails(scheduleId)) {
                // Batalkan alarm jika jadwal dihapus (asumsi ada 3 kali minum sehari)
                for (i in 0..5) {
                    alarmScheduler.cancel(medicineId * 100 + i)
                }
                _successMessage.value = "Jadwal dihapus"
                getSchedulesByDate(dateToRefresh)
            }
            _isLoading.value = false
        }
    }

    fun markAsTaken(detailId: Int, date: String) { // 'date' here is the "Viewed Date"
        viewModelScope.launch {

            val todayForDb = LocalDate.now().toString()
            val timeNow = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))

            if (historyRepository.markAsTaken(detailId, todayForDb, timeNow)) {


                getSchedulesByDate(date)

                if (date != todayForDb) {
                    getSchedulesByDate(todayForDb)
                }
            }
        }
    }
}