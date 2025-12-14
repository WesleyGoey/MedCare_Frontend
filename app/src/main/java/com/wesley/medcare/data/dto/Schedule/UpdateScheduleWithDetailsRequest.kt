package com.wesley.medcare.data.dto.Schedule

import com.wesley.medcare.data.dto.Medicine.DetailData

data class UpdateScheduleWithDetailsRequest(
    val details: List<DetailData>? = null,
    val medicineId: Int? = null,
    val scheduleType: String? = null,
    val startDate: String? = null
)