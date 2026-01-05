package com.wesley.medcare.data.dto.Medicine

data class ScheduleData(
    val details: List<DetailData>,
    val id: Int,
    val startDate: String,
    val status: Boolean
)