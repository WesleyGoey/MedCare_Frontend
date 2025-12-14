package com.wesley.medcare.data.dto.Medicine

data class MedicineSchedule(
    val details: List<MedicineDetail>,
    val id: Int,
    val scheduleType: String,
    val startDate: String
)