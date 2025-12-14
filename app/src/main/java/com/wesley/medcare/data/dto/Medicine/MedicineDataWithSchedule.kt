package com.wesley.medcare.data.dto.Medicine

data class MedicineDataWithSchedule(
    val dosage: String,
    val id: Int,
    val minStock: Int,
    val name: String,
    val notes: String,
    val schedules: List<MedicineSchedule>,
    val stock: Int,
    val type: String,
    val userId: Int
)