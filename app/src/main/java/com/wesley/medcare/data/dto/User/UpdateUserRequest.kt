package com.wesley.medcare.data.dto.User
data class UpdateUserRequest(
    val name: String? = null,
    val phone: String? = null,
    val age: Int? = null,
    val currentPassword: String? = null,
    val newPassword: String? = null
)
