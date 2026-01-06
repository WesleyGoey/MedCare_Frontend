package com.wesley.medcare.data.dto.Schedule

import com.wesley.medcare.data.dto.Medicine.DetailData

data class UpdateScheduleWithDetailsRequest(
    val details: List<TimeDetailData>? = null,
    val medicineId: Int? = null,
    val startDate: String? = null
)