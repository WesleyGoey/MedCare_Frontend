package com.wesley.medcare.ui.model

import com.wesley.medcare.data.dto.Schedule.MedicineData
import com.wesley.medcare.data.dto.Schedule.ScheduleData
import com.wesley.medcare.ui.model.History

data class ScheduleDetail(
    val dayOfWeek: Int,
    val id: Int,
    val medicine: MedicineData,
    val schedule: ScheduleData,
    val scheduleId: Int,
    val time: String,
    val history: List<History>? = null
)