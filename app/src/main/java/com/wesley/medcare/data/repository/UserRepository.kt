package com.wesley.medcare.data.repository

import com.wesley.medcare.data.dto.User.LoginRequest
import com.wesley.medcare.data.dto.User.RegisterRequest
import com.wesley.medcare.data.service.UserService
import com.wesley.medcare.ui.model.User

class UserRepository(private val userService: UserService) {

    suspend fun login(email: String, password: String): User {
        val response = userService.login(LoginRequest(email = email, password = password))
        if (response.isSuccessful) {
            val body = response.body()
            // TODO: map 'body' fields to your User model properly when response schema is known.
            return User() // placeholder mapping
        } else {
            throw Exception(response.errorBody()?.string() ?: "Login failed")
        }
    }

    suspend fun register(name: String, email: String, password: String, age: Int, phone: String): User {
        val request = RegisterRequest(name = name, email = email, password = password, age = age, phone = phone)
        val response = userService.register(request)
        if (response.isSuccessful) {
            val body = response.body()
            // TODO: map 'body' fields to your User model properly when response schema is known.
            return User() // placeholder mapping
        } else {
            throw Exception(response.errorBody()?.string() ?: "Register failed")
        }
    }
}
