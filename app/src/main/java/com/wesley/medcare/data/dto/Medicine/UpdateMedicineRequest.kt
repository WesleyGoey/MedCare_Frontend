package com.wesley.medcare.data.dto.Medicine

data class UpdateMedicineRequest(
    val name: String? = null,
    val type: String? = null,
    val dosage: String? = null,
    val stock: Int? = null,
    val minStock: Int? = null,
    val notes: String? = null
)
