package com.wesley.medcare.data.repository

import com.wesley.medcare.data.dto.Medicine.*
import com.wesley.medcare.data.service.MedicineService

class MedicineRepository(private val service: MedicineService) {
    suspend fun getAllMedicines(): GetAllMedicinesResponse {
        return service.getAllMedicines().body()!!
    }

    suspend fun getLowStock(threshold: Int? = null): GetLowStockResponse {
        return service.checkLowStock(threshold).body()!!
    }

    suspend fun getMedicineById(id: Int): GetMedicineByIdResponse {
        return service.getMedicineById(id).body()!!
    }

    suspend fun addMedicine(request: AddMedicineRequest): GetMedicineByIdResponse {
        return service.addMedicine(request).body()!!
    }

    suspend fun updateMedicine(id: Int, request: UpdateMedicineRequest): GetMedicineByIdResponse {
        return service.updateMedicine(id, request).body()!!
    }

    suspend fun deleteMedicine(id: Int): Boolean {
        val resp = service.deleteMedicine(id)
        return resp.isSuccessful
    }
}
