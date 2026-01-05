package com.wesley.medcare.data.dto.Medicine

data class MedicineData(
    val dosage: String,
    val id: Int,
    val minStock: Int,
    val name: String,
    val notes: String?,
    val status: Boolean,
    val stock: Int,
    val type: String,
    val userId: Int
)
