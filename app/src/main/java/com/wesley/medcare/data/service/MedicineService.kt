package com.wesley.medcare.data.service

import com.wesley.medcare.data.dto.Medicine.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface MedicineService {

    @GET("medicines")
    suspend fun getAllMedicines(
        @Query("page") page: Int? = null
    ): Response<GetAllMedicinesResponse>

    @GET("medicines/low-stock")
    suspend fun checkLowStock(
        @Query("threshold") threshold: Int? = null
    ): Response<GetLowStockResponse>

    @GET("medicines/{id}")
    suspend fun getMedicineById(
        @Path("id") id: Int
    ): Response<GetMedicineByIdResponse>

    @POST("medicines")
    suspend fun addMedicine(
        @Body request: AddMedicineRequest
    ): Response<Unit>

    @PATCH("medicines/{id}")
    suspend fun updateMedicine(
        @Path("id") id: Int,
        @Body request: UpdateMedicineRequest
    ): Response<Unit>

    @DELETE("medicines/{id}")
    suspend fun deleteMedicine(
        @Path("id") id: Int
    ): Response<Unit>
}
