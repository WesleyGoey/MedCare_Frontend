package com.wesley.medcare.data.repository

import android.content.Context
import android.util.Log
import com.wesley.medcare.data.dto.History.*
import com.wesley.medcare.data.service.HistoryService

class HistoryRepository(
    private val historyService: HistoryService,
    private val context: Context
) {
    private fun getToken(): String? {
        val sharedPreferences = context.getSharedPreferences("medcare_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", null)
    }

    // Mengambil 5 aktivitas terbaru (Sesuai Aturan Baru)
    suspend fun getRecentActivity(): GetAllHistoryResponse? {
        return try {
            val token = getToken() ?: return null
            val response = historyService.getRecentActivity("Bearer $token")
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e("HistoryRepo", "Error fetching recent activity", e)
            null
        }
    }

    suspend fun getWeeklyComplianceStatsTotal(): GetWeeklyComplianceStatsTotalResponse? {
        return try {
            val token = getToken() ?: return null
            val response = historyService.getWeeklyComplianceStatsTotal("Bearer $token")
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) { null }
    }

    suspend fun getWeeklyMissedDose(): GetWeeklyMissedDoseResponse? {
        return try {
            val token = getToken() ?: return null
            val response = historyService.getWeeklyMissedDose("Bearer $token")
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) { null }
    }

    suspend fun markAsTaken(detailId: Int, date: String, timeTaken: String): Boolean {
        return try {
            val token = getToken() ?: return false
            val request = MarkAsTakenRequest(date = date, timeTaken = timeTaken)
            val response = historyService.markAsTaken("Bearer $token", detailId, request)
            response.isSuccessful
        } catch (e: Exception) { false }
    }

    suspend fun undoMarkAsTaken(detailId: Int, date: String): Boolean {
        return try {
            val token = getToken() ?: return false
            val request = UndoMarkAsTakenRequest(date = date)
            val response = historyService.undoMarkAsTaken("Bearer $token", detailId, request)
            response.isSuccessful
        } catch (e: Exception) { false }
    }
}