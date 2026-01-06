package com.wesley.medcare.data.dto.Schedule

import com.google.gson.annotations.SerializedName
import com.wesley.medcare.ui.model.History

data class DetailData(
    val id: Int,
    val medicine: MedicineData,
    val schedule: ScheduleData,
    val scheduleId: Int,
    val time: String,
    @SerializedName("History")
    val history: List<History>? = null
)