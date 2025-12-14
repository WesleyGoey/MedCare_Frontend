package com.wesley.medcare.data.dto.Setting

data class UpdateSettingRequest(
    val alarmSound: String? = null,
    val notificationSound: String? = null
)