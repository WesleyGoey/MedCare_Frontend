package com.wesley.medcare.ui.model

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val password: String,
    val age: Int,
    val contact: String,
    val settingId: Int
)
