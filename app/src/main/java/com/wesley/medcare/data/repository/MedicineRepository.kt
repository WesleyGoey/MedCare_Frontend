// File: app/src/main/java/com/wesley/medcare/data/repository/MedicineRepository.kt
package com.wesley.medcare.data.repository

import android.content.Context
import android.util.Log
import com.wesley.medcare.data.dto.Medicine.AddMedicineRequest
import com.wesley.medcare.data.dto.Medicine.GetAllMedicinesResponse
import com.wesley.medcare.data.dto.Medicine.GetLowStockResponse
import com.wesley.medcare.data.dto.Medicine.GetMedicineByIdResponse
import com.wesley.medcare.data.service.MedicineService

class MedicineRepository(
    private val medicineService: MedicineService,
    private val context: Context
) {
    private fun getToken(): String? {
        val sharedPreferences = context.getSharedPreferences("medcare_prefs", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("auth_token", null)
        Log.d("MedicineRepository", "getToken: token=${if (token.isNullOrEmpty()) "NULL/EMPTY" else "EXISTS (${token.take(10)}...)"}")
        return token
    }

    suspend fun getAllMedicines(): GetAllMedicinesResponse? {
        return try {
            val token = getToken()
            if (token.isNullOrEmpty()) {
                Log.e("MedicineRepository", "Token not found")
                return null
            }

            val response = medicineService.getAllMedicines("Bearer $token")
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

    suspend fun addMedicine(
        name: String,
        type: String,
        dosage: String,
        stock: Int,
        minStock: Int,
        notes: String?
    ): Boolean {
        return try {
            val token = getToken()
            if (token.isNullOrEmpty()) {
                Log.e("MedicineRepository", "Token not found")
                return false
            }

            val request = AddMedicineRequest(
                name = name,
                type = type,
                dosage = dosage,
                stock = stock,
                minStock = minStock,
                notes = notes
            )
            val response = medicineService.addMedicine("Bearer $token", request)

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

    suspend fun getMedicineById(id: Int): GetMedicineByIdResponse? {
        return try {
            val token = getToken() ?: return null
            val response = medicineService.getMedicineById("Bearer $token", id)
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e("MedicineRepository", "Error getting medicine", e)
            null
        }
    }

    suspend fun getLowStock(): GetLowStockResponse? {
        return try {
            val token = getToken() ?: return null
            val response = medicineService.getLowStock("Bearer $token")
            if (response.isSuccessful) response.body() else null
        } catch (e: Exception) {
            Log.e("MedicineRepository", "Error getting low stock", e)
            null
        }
    }

    suspend fun updateMedicine(
        id: Int,
        name: String,
        type: String,
        dosage: String,
        stock: Int,
        minStock: Int,
        notes: String?
    ): Boolean {
        return try {
            val token = getToken() ?: return false
            val response = medicineService.updateMedicine(
                "Bearer $token", id, name, type, dosage, stock, minStock, notes
            )
            if (response.isSuccessful) {
                Log.d("MedicineRepository", "Medicine updated successfully")
                true
            } else {
                Log.e("MedicineRepository", "Update failed: ${response.errorBody()?.string()}")
                false
            }
        } catch (e: Exception) {
            Log.e("MedicineRepository", "Error updating medicine", e)
            false
        }
    }


    suspend fun deleteMedicine(id: Int): Boolean {
        return try {
            val token = getToken() ?: return false
            val response = medicineService.deleteMedicine("Bearer $token", id)
            response.isSuccessful
        } catch (e: Exception) {
            Log.e("MedicineRepository", "Error deleting medicine", e)
            false
        }
    }
}
