package com.wesley.medcare.data.dto.Medicine

import okhttp3.MultipartBody
import okhttp3.RequestBody

data class AddMedicineRequest(
    val dosage: RequestBody,
    val minStock: RequestBody,
    val name: RequestBody,
    val notes: RequestBody?,
    val stock: RequestBody,
    val type: RequestBody,
    val image: MultipartBody.Part?
)
