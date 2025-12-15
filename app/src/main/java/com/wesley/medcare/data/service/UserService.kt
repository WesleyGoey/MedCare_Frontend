package com.wesley.medcare.data.service

import com.wesley.medcare.data.dto.User.LoginRequest
import com.wesley.medcare.data.dto.User.LoginResponse
import com.wesley.medcare.data.dto.User.RegisterRequest
import com.wesley.medcare.data.dto.User.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body req: RegisterRequest): Response<RegisterResponse>
}

// Halo Test