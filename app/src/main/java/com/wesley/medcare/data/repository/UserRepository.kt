package com.wesley.medcare.data.repository

import com.wesley.medcare.data.dto.User.LoginRequest
import com.wesley.medcare.data.dto.User.LoginResponse
import com.wesley.medcare.data.dto.User.RegisterRequest
import com.wesley.medcare.data.dto.User.RegisterResponse
import com.wesley.medcare.data.service.UserService
import retrofit2.Response

class UserRepository(private val userService: UserService) {

    suspend fun login(email: String, password: String): Response<LoginResponse> {
        val req = LoginRequest(email = email, password = password)
        return userService.login(req)
    }

    suspend fun register(
        name: String,
        age: Int,
        phone: String,
        email: String,
        password: String
    ): Response<RegisterResponse> {
        val req = RegisterRequest(
            name = name,
            age = age,
            phone = phone,
            email = email,
            password = password
        )
        return userService.register(req)
    }
}
