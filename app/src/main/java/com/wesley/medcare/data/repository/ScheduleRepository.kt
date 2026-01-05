package com.wesley.medcare.data.repository

import android.content.Context
import android.util.Log
import com.wesley.medcare.data.dto.Schedule.CreateScheduleDetailsRequest
import com.wesley.medcare.data.dto.Schedule.CreateScheduleWithDetailsRequest
import com.wesley.medcare.data.dto.Schedule.GetAllSchedulesWithDetailsResponse
import com.wesley.medcare.data.dto.Schedule.GetScheduleWithDetailsByDateResponse
import com.wesley.medcare.data.dto.Schedule.GetScheduleWithDetailsByIdResponse
import com.wesley.medcare.data.dto.Schedule.TimeDetailData
import com.wesley.medcare.data.dto.Schedule.UpdateScheduleDetailsRequest
import com.wesley.medcare.data.dto.Schedule.UpdateScheduleWithDetailsRequest
import com.wesley.medcare.data.service.ScheduleService

class ScheduleRepository(
    private val scheduleService: ScheduleService,
    private val context: Context
) {
    private fun getToken(): String? {
        val sharedPreferences = context.getSharedPreferences("medcare_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)
        Log.d("ScheduleRepository", "getToken: token=${if (token.isNullOrEmpty()) "NULL/EMPTY" else "EXISTS (${token.take(10)}...)"}")
        return token
    }

    // Get all schedules with details
    suspend fun getAllScheduleWithDetails(): GetAllSchedulesWithDetailsResponse? {
        return try {
            val token = getToken()
            if (token.isNullOrEmpty()) {
                Log.e("ScheduleRepository", "Token not found")
                return null
            }

            val response = scheduleService.getAllScheduleWithDetails("Bearer $token")
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("ScheduleRepository", "Failed to get schedules: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ScheduleRepository", "Error getting schedules", e)
            null
        }
    }

    // Get schedule with details by date
    suspend fun getScheduleWithDetailsByDate(date: String): GetScheduleWithDetailsByDateResponse? {
        return try {
            val token = getToken()
            if (token.isNullOrEmpty()) {
                Log.e("ScheduleRepository", "Token not found")
                return null
            }

            val response = scheduleService.getScheduleWithDetailsByDate("Bearer $token", date)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("ScheduleRepository", "Failed to get schedule by date: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ScheduleRepository", "Error getting schedule by date", e)
            null
        }
    }

    // Get schedule with details by ID
    suspend fun getScheduleWithDetailsById(scheduleId: Int): GetScheduleWithDetailsByIdResponse? {
        return try {
            val token = getToken()
            if (token.isNullOrEmpty()) {
                Log.e("ScheduleRepository", "Token not found")
                return null
            }

            val response = scheduleService.getScheduleWithDetailsById("Bearer $token", scheduleId)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("ScheduleRepository", "Failed to get schedule by ID: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("ScheduleRepository", "Error getting schedule by ID", e)
            null
        }
    }

    // Create schedule with details (keseluruhan)
    suspend fun createScheduleWithDetails(
        medicineId: Int,
        startDate: String,
        scheduleType: String,
        details: List<TimeDetailData>
    ): Boolean {
        return try {
            val token = getToken()
            if (token.isNullOrEmpty()) {
                Log.e("ScheduleRepository", "Token not found")
                return false
            }

            val request = CreateScheduleWithDetailsRequest(
                medicineId = medicineId,
                startDate = startDate,
                details = details
            )

            val response = scheduleService.createScheduleWithDetails("Bearer $token", request)
            if (response.isSuccessful) {
                Log.d("ScheduleRepository", "Schedule created successfully")
                true
            } else {
                Log.e("ScheduleRepository", "Failed to create schedule: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("ScheduleRepository", "Error creating schedule", e)
            false
        }
    }

    // Create schedule details (jam)
    suspend fun createScheduleDetails(
        scheduleId: Int,
        details: List<TimeDetailData>
    ): Boolean {
        return try {
            val token = getToken()
            if (token.isNullOrEmpty()) {
                Log.e("ScheduleRepository", "Token not found")
                return false
            }

            val request = CreateScheduleDetailsRequest()
            request.addAll(details)

            val response = scheduleService.createScheduleDetails("Bearer $token", scheduleId, request)
            if (response.isSuccessful) {
                Log.d("ScheduleRepository", "Schedule details created successfully")
                true
            } else {
                Log.e("ScheduleRepository", "Failed to create schedule details: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("ScheduleRepository", "Error creating schedule details", e)
            false
        }
    }

    // Update schedule with details (keseluruhan)
    suspend fun updateScheduleWithDetails(
        scheduleId: Int,
        medicineId: Int?,
        startDate: String?,
        scheduleType: String?,
        details: List<com.wesley.medcare.data.dto.Medicine.DetailData>?
    ): Boolean {
        return try {
            val token = getToken()
            if (token.isNullOrEmpty()) {
                Log.e("ScheduleRepository", "Token not found")
                return false
            }

            val request = UpdateScheduleWithDetailsRequest(
                medicineId = medicineId,
                startDate = startDate,
                details = details
            )

            val response = scheduleService.updateScheduleWithDetails("Bearer $token", scheduleId, request)
            if (response.isSuccessful) {
                Log.d("ScheduleRepository", "Schedule updated successfully")
                true
            } else {
                Log.e("ScheduleRepository", "Failed to update schedule: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("ScheduleRepository", "Error updating schedule", e)
            false
        }
    }

    // Update schedule details (jam)
    suspend fun updateScheduleDetails(
        detailId: Int,
        time: String
    ): Boolean {
        return try {
            val token = getToken()
            if (token.isNullOrEmpty()) {
                Log.e("ScheduleRepository", "Token not found")
                return false
            }

            val request = UpdateScheduleDetailsRequest(
                time = time
            )

            val response = scheduleService.updateScheduleDetails("Bearer $token", detailId, request)
            if (response.isSuccessful) {
                Log.d("ScheduleRepository", "Schedule details updated successfully")
                true
            } else {
                Log.e("ScheduleRepository", "Failed to update schedule details: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("ScheduleRepository", "Error updating schedule details", e)
            false
        }
    }

    // Delete schedule with details (keseluruhan)
    suspend fun deleteScheduleWithDetails(scheduleId: Int): Boolean {
        return try {
            val token = getToken()
            if (token.isNullOrEmpty()) {
                Log.e("ScheduleRepository", "Token not found")
                return false
            }

            val response = scheduleService.deleteScheduleWithDetails("Bearer $token", scheduleId)
            if (response.isSuccessful) {
                Log.d("ScheduleRepository", "Schedule deleted successfully")
                true
            } else {
                Log.e("ScheduleRepository", "Failed to delete schedule: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("ScheduleRepository", "Error deleting schedule", e)
            false
        }
    }

    // Delete schedule details (jam)
    suspend fun deleteScheduleDetails(detailId: Int): Boolean {
        return try {
            val token = getToken()
            if (token.isNullOrEmpty()) {
                Log.e("ScheduleRepository", "Token not found")
                return false
            }

            val response = scheduleService.deleteScheduleDetails("Bearer $token", detailId)
            if (response.isSuccessful) {
                Log.d("ScheduleRepository", "Schedule details deleted successfully")
                true
            } else {
                Log.e("ScheduleRepository", "Failed to delete schedule details: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("ScheduleRepository", "Error deleting schedule details", e)
            false
        }
    }
}

