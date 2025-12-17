package com.wesley.medcare.data.service

import com.wesley.medcare.data.dto.History.GetAllHistoryResponse
import com.wesley.medcare.data.dto.History.GetWeeklyComplianceStatsResponse
import com.wesley.medcare.data.dto.History.GetWeeklyComplianceStatsTotalResponse
import com.wesley.medcare.data.dto.History.GetWeeklyMissedDoseResponse
import com.wesley.medcare.data.dto.History.MarkAsTakenRequest
import com.wesley.medcare.data.dto.History.SkipOccurrenceRequest
import com.wesley.medcare.data.dto.History.UndoMarkAsTakenRequest
import retrofit2.Response
import retrofit2.http.*

interface HistoryService {
    @GET("history")
    suspend fun getAllHistory(
        @Header("Authorization") token: String
    ): Response<GetAllHistoryResponse>

    @GET("history/compliance")
    suspend fun getWeeklyComplianceStatsTotal(
        @Header("Authorization") token: String
    ): Response<GetWeeklyComplianceStatsTotalResponse>

    @GET("history/missed")
    suspend fun getWeeklyMissedDose(
        @Header("Authorization") token: String
    ): Response<GetWeeklyMissedDoseResponse>

    @GET("history/weekly-stats")
    suspend fun getWeeklyComplianceStats(
        @Header("Authorization") token: String
    ): Response<GetWeeklyComplianceStatsResponse>

    @GET("history/recent")
    suspend fun getRecentActivity(
        @Header("Authorization") token: String
    ): Response<GetAllHistoryResponse>

    @PATCH("history/details/{detailId}/mark-taken")
    suspend fun markAsTaken(
        @Header("Authorization") token: String,
        @Path("detailId") detailId: Int,
        @Body body: MarkAsTakenRequest
    ): Response<Unit>

    @POST("history/details/{detailId}/skip")
    suspend fun skipOccurrence(
        @Header("Authorization") token: String,
        @Path("detailId") detailId: Int,
        @Body body: SkipOccurrenceRequest
    ): Response<Unit>

    @PATCH("history/details/{detailId}/undo-taken")
    suspend fun undoMarkAsTaken(
        @Header("Authorization") token: String,
        @Path("detailId") detailId: Int,
        @Body body: UndoMarkAsTakenRequest
    ): Response<Unit>
}