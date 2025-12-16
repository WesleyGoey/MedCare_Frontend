package com.wesley.medcare.data.service

import com.wesley.medcare.data.dto.Medicine.AddMedicineRequest
import com.wesley.medcare.data.dto.Medicine.GetAllMedicinesResponse
import com.wesley.medcare.data.dto.Medicine.GetLowStockResponse
import com.wesley.medcare.data.dto.Medicine.GetMedicineByIdResponse
import com.wesley.medcare.data.dto.Medicine.UpdateMedicineRequest
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface MedicineService {
    @GET("medicine")
    suspend fun getAllMedicines(): Response<GetAllMedicinesResponse>

    @GET("medicine/{id}")
    suspend fun getMedicineById(@Path("id") id: Int): Response<GetMedicineByIdResponse>

    @GET("medicine/low-stock")
    suspend fun getLowStock(): Response<GetLowStockResponse>

    @Multipart
    @POST("medicine")
    suspend fun addMedicine(
        @Part("name") name: RequestBody,
        @Part("type") type: RequestBody,
        @Part("dosage") dosage: RequestBody,
        @Part("stock") stock: RequestBody,
        @Part("minStock") minStock: RequestBody,
        @Part("notes") notes: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Response<Unit>

    @Multipart
    @PUT("medicine/{id}")
    suspend fun updateMedicine(
        @Path("id") id: Int,
        @Part("name") name: RequestBody?,
        @Part("type") type: RequestBody?,
        @Part("dosage") dosage: RequestBody?,
        @Part("stock") stock: RequestBody?,
        @Part("minStock") minStock: RequestBody?,
        @Part("notes") notes: RequestBody?,
        @Part image: MultipartBody.Part?
    ): Response<Unit>

    @DELETE("medicine/{id}")
    suspend fun deleteMedicine(@Path("id") id: Int): Response<Unit>
}
