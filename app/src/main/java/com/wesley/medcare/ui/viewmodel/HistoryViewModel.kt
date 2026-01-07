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

    /**
     * Membersihkan pesan setelah ditampilkan di UI.
     */
    fun clearMessage() {
        _message.value = null
    }

    /**
     * Memperbarui seluruh data dashboard riwayat.
     */
    fun refreshDashboard() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                fetchCompliance()
                fetchMissedDoses()
                fetchRecentActivity()
            } catch (e: Exception) {
                Log.e("HistoryVM", "Error refreshing dashboard", e)
                _message.value = "Gagal memuat data riwayat"
            } finally {
                _isLoading.value = false
            }
        }
    }

    /**
     * Menandai obat sebagai sudah diminum (Taken).
     * Sesuai aturan: Hanya bisa dilakukan di hari ini.
     */
    fun markAsTaken(detailId: Int, date: String, timeTaken: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.markAsTaken(detailId, date, timeTaken)
            if (success) {
                _message.value = "Berhasil: Obat telah diminum"
                refreshDashboard()
            } else {
                _message.value = "Gagal: Hanya bisa dilakukan di hari ini"
            }
            _isLoading.value = false
        }
    }

    /**
     * Melewati jadwal minum obat (Skip).
     */
    fun skipOccurrence(detailId: Int, date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.skipOccurrence(detailId, date)
            if (success) {
                _message.value = "Jadwal berhasil dilewati"
                refreshDashboard()
            } else {
                _message.value = "Gagal melewati jadwal"
            }
            _isLoading.value = false
        }
    }

    /**
     * Membatalkan status diminum dan mengembalikan stok obat.
     * Sesuai aturan: Nama fungsi undoMarkAsTaken dan hanya bisa di hari ini.
     */
    fun undoMarkAsTaken(detailId: Int, date: String) {
        viewModelScope.launch {
            _isLoading.value = true
            val success = repository.undoMarkAsTaken(detailId, date)
            if (success) {
                _message.value = "Aksi dibatalkan, stok dikembalikan"
                refreshDashboard()
            } else {
                _message.value = "Gagal: Hanya bisa membatalkan aksi hari ini"
            }
            _isLoading.value = false
        }
    }

    /**
     * Statistik kepatuhan mingguan (Senin - Minggu).
     */
    private suspend fun fetchCompliance() {
        val response = repository.getWeeklyComplianceStatsTotal()
        _compliancePercentage.value = response?.data ?: 0
    }

    /**
     * Jumlah dosis terlewat mingguan (Senin - Minggu).
     */
    private suspend fun fetchMissedDoses() {
        val response = repository.getWeeklyMissedDose()
        _missedDosesCount.value = response?.data ?: 0
    }

    /**
     * Mengambil riwayat terbaru.
     * Aturan: Mengambil 5 terbaru yang berstatus DONE atau MISSED.
     */
    // ✅ Revisi fetchRecentActivity di HistoryViewModel.kt
    private suspend fun fetchRecentActivity() {
        val response = repository.getRecentActivity()

        // JANGAN di-sort lagi di sini agar urutan 'updatedAt' dari backend tidak rusak
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

        // Hanya ambil yang DONE atau MISSED (sebagai double-check)
        _recentActivityList.value = mappedList.filter {
            it.status == "DONE" || it.status == "MISSED"
        }
    }
}