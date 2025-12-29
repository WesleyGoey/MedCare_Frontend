// File: app/src/main/java/com/wesley/medcare/data/service/MedicineService.kt
package com.wesley.medcare.data.service

import com.wesley.medcare.data.dto.Medicine.AddMedicineRequest
import com.wesley.medcare.data.dto.Medicine.GetAllMedicinesResponse
import com.wesley.medcare.data.dto.Medicine.GetLowStockResponse
import com.wesley.medcare.data.dto.Medicine.GetMedicineByIdResponse
import retrofit2.Response
import retrofit2.http.*

interface MedicineService {
    @GET("medicines")
    suspend fun getAllMedicines(
        @Header("Authorization") token: String
    ): Response<GetAllMedicinesResponse>

    @GET("medicines/{id}")
    suspend fun getMedicineById(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<GetMedicineByIdResponse>

    @GET("medicines/low-stock")
    suspend fun getLowStock(
        @Header("Authorization") token: String
    ): Response<GetLowStockResponse>

    @POST("medicines")
    suspend fun addMedicine(
        @Header("Authorization") token: String,
        @Body body: AddMedicineRequest
    ): Response<Unit>

    @FormUrlEncoded
    @PATCH("medicines/{id}")
    suspend fun updateMedicine(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Field("name") name: String,
        @Field("type") type: String,
        @Field("dosage") dosage: String,
        @Field("stock") stock: Int,
        @Field("minStock") minStock: Int,
        @Field("notes") notes: String?
    ): Response<Unit>

    @DELETE("medicines/{id}")
    suspend fun deleteMedicine(
        @Header("Authorization") token: String,
        @Path("id") id: Int
    ): Response<Unit>
}
