package com.wesley.medcare.ui.model

import com.wesley.medcare.data.dto.Schedule.MedicineData
import com.wesley.medcare.data.dto.Schedule.ScheduleData

data class ScheduleDetail(
    val dayOfWeek: Int,
    val id: Int,
    val medicine: MedicineData,
    val schedule: ScheduleData,
    val scheduleId: Int,
    val time: String
)