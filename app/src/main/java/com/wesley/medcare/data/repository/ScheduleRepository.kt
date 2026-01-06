package com.wesley.medcare.data.repository

import android.content.Context
import com.wesley.medcare.data.dto.Schedule.*
import com.wesley.medcare.data.service.ScheduleService

class ScheduleRepository(
    private val scheduleService: ScheduleService,
    private val context: Context
) {
    private fun getToken(): String? {
        val sharedPreferences = context.getSharedPreferences("medcare_prefs", Context.MODE_PRIVATE)
        return sharedPreferences.getString("auth_token", null)
    }

    suspend fun getAllScheduleWithDetails(): GetAllSchedulesWithDetailsResponse? {
        val token = getToken() ?: return null
        val response = scheduleService.getAllScheduleWithDetails("Bearer $token")
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getScheduleWithDetailsByDate(date: String): GetScheduleWithDetailsByDateResponse? {
        val token = getToken() ?: return null
        val response = scheduleService.getScheduleWithDetailsByDate("Bearer $token", date)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun getScheduleWithDetailsById(scheduleId: Int): GetScheduleWithDetailsByIdResponse? {
        val token = getToken() ?: return null
        val response = scheduleService.getScheduleWithDetailsById("Bearer $token", scheduleId)
        return if (response.isSuccessful) response.body() else null
    }

    suspend fun createScheduleWithDetails(medicineId: Int, startDate: String, details: List<TimeDetailData>): Boolean {
        val token = getToken() ?: return false
        val request = CreateScheduleWithDetailsRequest(details, medicineId, startDate)
        return scheduleService.createScheduleWithDetails("Bearer $token", request).isSuccessful
    }

    suspend fun updateScheduleWithDetails(scheduleId: Int, medicineId: Int, startDate: String, details: List<TimeDetailData>): Boolean {
        val token = getToken() ?: return false
        // Pastikan medicineId tidak null saat membuat request
        val request = UpdateScheduleWithDetailsRequest(details, medicineId, startDate)
        return try {
            val response = scheduleService.updateScheduleWithDetails("Bearer $token", scheduleId, request)
            response.isSuccessful
        } catch (e: Exception) {
            false
        }
    }

    suspend fun deleteScheduleWithDetails(scheduleId: Int): Boolean {
        val token = getToken() ?: return false
        return scheduleService.deleteScheduleWithDetails("Bearer $token", scheduleId).isSuccessful
    }
}