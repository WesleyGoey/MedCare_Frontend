package com.wesley.medcare.data.repository

import android.content.Context
import android.util.Log
import com.wesley.medcare.data.dto.History.GetAllHistoryResponse
import com.wesley.medcare.data.dto.History.GetWeeklyComplianceStatsResponse
import com.wesley.medcare.data.dto.History.GetWeeklyComplianceStatsTotalResponse
import com.wesley.medcare.data.dto.History.GetWeeklyMissedDoseResponse
import com.wesley.medcare.data.dto.History.MarkAsTakenRequest
import com.wesley.medcare.data.dto.History.SkipOccurrenceRequest
import com.wesley.medcare.data.dto.History.UndoMarkAsTakenRequest
import com.wesley.medcare.data.service.HistoryService

class HistoryRepository(
    private val historyService: HistoryService,
    private val context: Context
) {
    private fun getToken(): String? {
        val sharedPreferences = context.getSharedPreferences("medcare_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)
        Log.d("HistoryRepository", "getToken: token=${if (token.isNullOrEmpty()) "NULL/EMPTY" else "EXISTS (${token.take(10)}...)"}")
        return token
    }

    suspend fun getAllHistory(): GetAllHistoryResponse? {
        return try {
            val token = getToken()
            if (token.isNullOrEmpty()) {
                Log.e("HistoryRepository", "Token not found")
                return null
            }

            val response = historyService.getAllHistory("Bearer $token")
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("HistoryRepository", "Failed to get history: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("HistoryRepository", "Error getting history", e)
            null
        }
    }

    suspend fun getWeeklyComplianceStatsTotal(): GetWeeklyComplianceStatsTotalResponse? {
        return try {
            val token = getToken() ?: return null
            val response = historyService.getWeeklyComplianceStatsTotal("Bearer $token")

            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("HistoryRepository", "Failed to get compliance total: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("HistoryRepository", "Error getting compliance total", e)
            null
        }
    }

    suspend fun getWeeklyMissedDose(): GetWeeklyMissedDoseResponse? {
        return try {
            val token = getToken() ?: return null
            val response = historyService.getWeeklyMissedDose("Bearer $token")

            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("HistoryRepository", "Failed to get missed doses: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("HistoryRepository", "Error getting missed doses", e)
            null
        }
    }

    suspend fun getWeeklyComplianceStats(): GetWeeklyComplianceStatsResponse? {
        return try {
            val token = getToken() ?: return null
            val response = historyService.getWeeklyComplianceStats("Bearer $token")

            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("HistoryRepository", "Failed to get weekly stats: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("HistoryRepository", "Error getting weekly stats", e)
            null
        }
    }

    suspend fun getRecentActivity(): GetAllHistoryResponse? {
        return try {
            val token = getToken() ?: return null
            val response = historyService.getRecentActivity("Bearer $token")

            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("HistoryRepository", "Failed to get recent activity: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("HistoryRepository", "Error getting recent activity", e)
            null
        }
    }

    suspend fun markAsTaken(detailId: Int, date: String, timeTaken: String): Boolean {
        return try {
            val token = getToken() ?: return false
            val request = MarkAsTakenRequest(date = date, timeTaken = timeTaken)

            val response = historyService.markAsTaken("Bearer $token", detailId, request)

            if (response.isSuccessful) {
                Log.d("HistoryRepository", "Marked as taken successfully")
                true
            } else {
                Log.e("HistoryRepository", "Failed to mark as taken: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("HistoryRepository", "Error marking as taken", e)
            false
        }
    }

    suspend fun skipOccurrence(detailId: Int, date: String): Boolean {
        return try {
            val token = getToken() ?: return false
            val request = SkipOccurrenceRequest(date = date)

            val response = historyService.skipOccurrence("Bearer $token", detailId, request)

            if (response.isSuccessful) {
                Log.d("HistoryRepository", "Skipped successfully")
                true
            } else {
                Log.e("HistoryRepository", "Failed to skip: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("HistoryRepository", "Error skipping", e)
            false
        }
    }

    suspend fun undoMarkAsTaken(detailId: Int): Boolean {
        return try {
            val token = getToken() ?: return false
            val currentDate = java.time.LocalDate.now().toString()
            val request = UndoMarkAsTakenRequest(
                date = currentDate
            )

            val response = historyService.undoMarkAsTaken("Bearer $token", detailId, request)

            if (response.isSuccessful) {
                Log.d("HistoryRepository", "Undo successful")
                true
            } else {
                Log.e("HistoryRepository", "Failed to undo: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("HistoryRepository", "Error undoing", e)
            false
        }
    }
}