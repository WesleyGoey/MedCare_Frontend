package com.wesley.medcare.data.container

import com.wesley.medcare.data.repository.AuthenticationRepository
import com.wesley.medcare.data.repository.AuthenticationRepositoryInterface
import com.wesley.medcare.data.service.AuthenticationAPIService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AppContainer {
    private val BASE_URL = "YOUR_BASE_URL_HERE"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val authService: AuthenticationAPIService = retrofit.create(AuthenticationAPIService::class.java)

    val authRepository: AuthenticationRepositoryInterface = AuthenticationRepository(authService)
}
