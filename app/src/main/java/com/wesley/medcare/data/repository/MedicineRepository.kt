package com.wesley.medcare.data.repository

import android.net.Uri
import android.util.Log
import com.wesley.medcare.data.dto.Medicine.GetAllMedicinesResponse
import com.wesley.medcare.data.dto.Medicine.GetLowStockResponse
import com.wesley.medcare.data.dto.Medicine.GetMedicineByIdResponse
import com.wesley.medcare.data.service.MedicineService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class MedicineRepository(private val medicineService: MedicineService) {
    suspend fun getAllMedicines(): GetAllMedicinesResponse? {
        return try {
            val response = medicineService.getAllMedicines()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("MedicineRepository", "Failed to get medicines: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MedicineRepository", "Error getting medicines", e)
            null
        }
    }

    suspend fun getMedicineById(id: Int): GetMedicineByIdResponse? {
        return try {
            val response = medicineService.getMedicineById(id)
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("MedicineRepository", "Failed to get medicine: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MedicineRepository", "Error getting medicine", e)
            null
        }
    }

    suspend fun getLowStock(): GetLowStockResponse? {
        return try {
            val response = medicineService.getLowStock()
            if (response.isSuccessful) {
                response.body()
            } else {
                Log.e("MedicineRepository", "Failed to get low stock: ${response.errorBody()?.string()}")
                null
            }
        } catch (e: Exception) {
            Log.e("MedicineRepository", "Error getting low stock", e)
            null
        }
    }

    suspend fun addMedicine(
        name: String,
        type: String,
        dosage: String,
        stock: Int,
        minStock: Int,
        notes: String?,
        imageFile: File?
    ): Boolean {
        return try {
            val nameBody = name.toRequestBody("text/plain".toMediaTypeOrNull())
            val typeBody = type.toRequestBody("text/plain".toMediaTypeOrNull())
            val dosageBody = dosage.toRequestBody("text/plain".toMediaTypeOrNull())
            val stockBody = stock.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val minStockBody = minStock.toString().toRequestBody("text/plain".toMediaTypeOrNull())
            val notesBody = notes?.toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = imageFile?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", it.name, requestFile)
            }

            val response = medicineService.addMedicine(
                name = nameBody,
                type = typeBody,
                dosage = dosageBody,
                stock = stockBody,
                minStock = minStockBody,
                notes = notesBody,
                image = imagePart
            )

            if (response.isSuccessful) {
                Log.d("MedicineRepository", "Medicine added successfully")
                true
            } else {
                Log.e("MedicineRepository", "Failed to add medicine: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("MedicineRepository", "Error adding medicine", e)
            false
        }
    }

    suspend fun updateMedicine(
        id: Int,
        name: String?,
        type: String?,
        dosage: String?,
        stock: Int?,
        minStock: Int?,
        notes: String?,
        imageFile: File?
    ): Boolean {
        return try {
            val nameBody = name?.toRequestBody("text/plain".toMediaTypeOrNull())
            val typeBody = type?.toRequestBody("text/plain".toMediaTypeOrNull())
            val dosageBody = dosage?.toRequestBody("text/plain".toMediaTypeOrNull())
            val stockBody = stock?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val minStockBody = minStock?.toString()?.toRequestBody("text/plain".toMediaTypeOrNull())
            val notesBody = notes?.toRequestBody("text/plain".toMediaTypeOrNull())

            val imagePart = imageFile?.let {
                val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", it.name, requestFile)
            }

            val response = medicineService.updateMedicine(
                id = id,
                name = nameBody,
                type = typeBody,
                dosage = dosageBody,
                stock = stockBody,
                minStock = minStockBody,
                notes = notesBody,
                image = imagePart
            )

            if (response.isSuccessful) {
                Log.d("MedicineRepository", "Medicine updated successfully")
                true
            } else {
                Log.e("MedicineRepository", "Failed to update medicine: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("MedicineRepository", "Error updating medicine", e)
            false
        }
    }

    suspend fun deleteMedicine(id: Int): Boolean {
        return try {
            val response = medicineService.deleteMedicine(id)
            if (response.isSuccessful) {
                Log.d("MedicineRepository", "Medicine deleted successfully")
                true
            } else {
                Log.e("MedicineRepository", "Failed to delete medicine: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("MedicineRepository", "Error deleting medicine", e)
            false
        }
    }
}
