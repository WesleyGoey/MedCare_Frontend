package com.wesley.medcare.data.dto.Medicine

data class AddMedicineRequest(
    val dosage: String,
    val image: String? = null,
    val minStock: Int,
    val name: String,
    val notes: String? = null,
    val stock: Int,
    val type: String
)