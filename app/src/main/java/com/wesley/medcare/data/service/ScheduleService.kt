package com.wesley.medcare.data.service

import com.wesley.medcare.data.dto.Schedule.*
import retrofit2.Response
import retrofit2.http.*

interface ScheduleService {
    @GET("schedules")
    suspend fun getAllScheduleWithDetails(
        @Header("Authorization") token: String
    ): Response<GetAllSchedulesWithDetailsResponse>

    @GET("schedules/by-date")
    suspend fun getScheduleWithDetailsByDate(
        @Header("Authorization") token: String,
        @Query("date") date: String
    ): Response<GetScheduleWithDetailsByDateResponse>

    @GET("schedules/{scheduleId}")
    suspend fun getScheduleWithDetailsById(
        @Header("Authorization") token: String,
        @Path("scheduleId") scheduleId: Int
    ): Response<GetScheduleWithDetailsByIdResponse>

    @POST("schedules")
    suspend fun createScheduleWithDetails(
        @Header("Authorization") token: String,
        @Body body: CreateScheduleWithDetailsRequest
    ): Response<Unit>

    @PATCH("schedules/{scheduleId}")
    suspend fun updateScheduleWithDetails(
        @Header("Authorization") token: String,
        @Path("scheduleId") scheduleId: Int,
        @Body body: UpdateScheduleWithDetailsRequest
    ): Response<Unit>

    @DELETE("schedules/{scheduleId}")
    suspend fun deleteScheduleWithDetails(
        @Header("Authorization") token: String,
        @Path("scheduleId") scheduleId: Int
    ): Response<Unit>
}