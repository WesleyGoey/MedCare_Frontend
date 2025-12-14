package com.wesley.medcare.data.dto.Schedule

data class CreateScheduleWithDetailsRequest(
    val details: List<TimeDetailData>,
    val medicineId: Int,
    val scheduleType: String,
    val startDate: String
)