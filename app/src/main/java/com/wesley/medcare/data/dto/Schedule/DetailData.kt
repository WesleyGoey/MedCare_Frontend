package com.wesley.medcare.data.dto.Schedule

data class DetailData(
    val dayOfWeek: Int,
    val id: Int,
    val medicine: MedicineData,
    val schedule: ScheduleData,
    val scheduleId: Int,
    val time: String
)