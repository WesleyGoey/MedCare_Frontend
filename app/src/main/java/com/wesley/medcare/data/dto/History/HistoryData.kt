package com.wesley.medcare.data.dto.History

data class HistoryData(
    val id: Int,
    val medicineName: String,
    val scheduledDate: String,
    val scheduledTime: String,
    val status: String,
    val timeTaken: String
)