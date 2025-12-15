package com.wesley.medcare.data.service

import com.wesley.medcare.data.dto.User.GetProfileResponse
import com.wesley.medcare.data.dto.User.LoginRequest
import com.wesley.medcare.data.dto.User.LoginResponse
import com.wesley.medcare.data.dto.User.RegisterRequest
import com.wesley.medcare.data.dto.User.RegisterResponse
import com.wesley.medcare.data.dto.User.UpdateUserRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.PUT

interface UserService {
    @POST("login")
    suspend fun login(@Body req: LoginRequest): Response<LoginResponse>

    @POST("register")
    suspend fun register(@Body req: RegisterRequest): Response<RegisterResponse>

    @GET("profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<GetProfileResponse>

    @PATCH("profile")
    suspend fun editProfile(
        @Header("Authorization") token: String,
        @Body req: UpdateUserRequest
    ): Response<GetProfileResponse>
}
