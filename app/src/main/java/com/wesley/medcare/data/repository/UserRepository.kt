package com.wesley.medcare.data.repository

import com.wesley.medcare.data.dto.User.LoginRequest
import com.wesley.medcare.data.dto.User.LoginResponse
import com.wesley.medcare.data.dto.User.RegisterRequest
import com.wesley.medcare.data.service.UserService
import com.wesley.medcare.ui.model.User
import retrofit2.Response

class UserRepository(private val userService: UserService) {

    suspend fun login(email: String, password: String): Response<LoginResponse> {
        val req = LoginRequest(email = email, password = password)
        return userService.login(req)
    }

    suspend fun register(email: String, password: String): Response<LoginResponse> {
        val req = LoginRequest(email = email, password = password)
        return userService.login(req)
    }
}
