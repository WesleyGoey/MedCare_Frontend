package com.wesley.medcare.data.dto.User

data class RegisterRequest(
    val age: Int,
    val email: String,
    val name: String,
    val password: String,
    val phone: String
)