package com.wesley.medcare.data.service

import com.wesley.medcare.data.dto.Schedule.CreateScheduleDetailsRequest
import com.wesley.medcare.data.dto.Schedule.CreateScheduleWithDetailsRequest
import com.wesley.medcare.data.dto.Schedule.GetAllSchedulesWithDetailsResponse
import com.wesley.medcare.data.dto.Schedule.GetScheduleWithDetailsByDateResponse
import com.wesley.medcare.data.dto.Schedule.GetScheduleWithDetailsByIdResponse
import com.wesley.medcare.data.dto.Schedule.UpdateScheduleDetailsRequest
import com.wesley.medcare.data.dto.Schedule.UpdateScheduleWithDetailsRequest
import retrofit2.Response
import retrofit2.http.*

interface ScheduleService {
    // Get all schedules with details
    @GET("schedules")
    suspend fun getAllScheduleWithDetails(
        @Header("Authorization") token: String
    ): Response<GetAllSchedulesWithDetailsResponse>

    // Get schedule with details by date
    @GET("schedules/by-date")
    suspend fun getScheduleWithDetailsByDate(
        @Header("Authorization") token: String,
        @Query("date") date: String // Format: YYYY-MM-DD
    ): Response<GetScheduleWithDetailsByDateResponse>

    // Get schedule with details by ID
    @GET("schedules/{scheduleId}")
    suspend fun getScheduleWithDetailsById(
        @Header("Authorization") token: String,
        @Path("scheduleId") scheduleId: Int
    ): Response<GetScheduleWithDetailsByIdResponse>

    // Create schedule with details (keseluruhan)
    @POST("schedules")
    suspend fun createScheduleWithDetails(
        @Header("Authorization") token: String,
        @Body body: CreateScheduleWithDetailsRequest
    ): Response<Unit>

    // Create schedule details (jam)
    @POST("schedules/{scheduleId}/details")
    suspend fun createScheduleDetails(
        @Header("Authorization") token: String,
        @Path("scheduleId") scheduleId: Int,
        @Body body: CreateScheduleDetailsRequest
    ): Response<Unit>

    // Update schedule with details (keseluruhan)
    @PATCH("schedules/{scheduleId}")
    suspend fun updateScheduleWithDetails(
        @Header("Authorization") token: String,
        @Path("scheduleId") scheduleId: Int,
        @Body body: UpdateScheduleWithDetailsRequest
    ): Response<Unit>

    // Update schedule details (jam)
    @PATCH("schedules/details/{detailId}")
    suspend fun updateScheduleDetails(
        @Header("Authorization") token: String,
        @Path("detailId") detailId: Int,
        @Body body: UpdateScheduleDetailsRequest
    ): Response<Unit>

    // Delete schedule with details (keseluruhan)
    @DELETE("schedules/{scheduleId}")
    suspend fun deleteScheduleWithDetails(
        @Header("Authorization") token: String,
        @Path("scheduleId") scheduleId: Int
    ): Response<Unit>

    // Delete schedule details (jam)
    @DELETE("schedules/details/{detailId}")
    suspend fun deleteScheduleDetails(
        @Header("Authorization") token: String,
        @Path("detailId") detailId: Int
    ): Response<Unit>
}