package com.wesley.medcare.ui.model

import com.wesley.medcare.data.dto.Schedule.MedicineData
import com.wesley.medcare.data.dto.Schedule.ScheduleData
import com.wesley.medcare.ui.model.History

data class ScheduleDetail(
    val id: Int,
    val scheduleId: Int,
    val time: String,
)