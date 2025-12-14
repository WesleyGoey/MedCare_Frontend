package com.wesley.medcare.data.dto.User

data class UpdateUserRequest(
    val age: Int? = null,
    val email: String? = null,
    val name: String? = null,
    val phone: String? = null,
    val settingId: Int? = null
)
