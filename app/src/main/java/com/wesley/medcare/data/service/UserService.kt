package com.wesley.medcare.data.service

import com.wesley.medcare.data.dto.User.LoginRequest
import com.wesley.medcare.data.dto.User.RegisterRequest
import com.wesley.medcare.ui.model.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthenticationAPIService {
    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): Response<User>

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): Response<User>
}


//    @GET("search.php")
//    suspend fun getArtist(
//        @Query("s") artistName: String
//    ): Response<ResponseArtist>