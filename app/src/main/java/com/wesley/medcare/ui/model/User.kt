package com.wesley.medcare.ui.model

data class User(
    val id: Int = 0,
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val age: Int = 0,
    val contact: String = "",
    val settingId: Int = 0,
    val token: String = "",
    val isError: Boolean = false,
    val errorMessage: String? = null
)
