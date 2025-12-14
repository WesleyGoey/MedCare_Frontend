package com.wesley.medcare.data.dto.Schedule

import com.wesley.medcare.data.dto.Medicine.DetailData

data class GetScheduleWithDetailsByIdResponse(
    val `data`: List<DetailData>
)