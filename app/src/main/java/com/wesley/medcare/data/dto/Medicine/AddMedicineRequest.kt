package com.wesley.medcare.data.dto.Medicine

data class AddMedicineRequest(
    val name: String,
    val type: String,
    val dosage: String,
    val stock: Int,
    val minStock: Int,
    val notes: String? = null
)
