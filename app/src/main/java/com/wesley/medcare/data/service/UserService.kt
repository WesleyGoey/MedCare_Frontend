package com.wesley.medcare.data.service

import com.wesley.medcare.data.dto.User.LoginRequest
import com.wesley.medcare.data.dto.User.LoginResponse
import com.wesley.medcare.data.dto.User.RegisterRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface UserService {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<Map<String, Any>>
}
