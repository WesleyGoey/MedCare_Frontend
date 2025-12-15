package com.wesley.medcare.data.container

import com.google.gson.GsonBuilder
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.wesley.medcare.data.service.UserService
import com.wesley.medcare.data.repository.UserRepository

class AppContainer {
    companion object {
        const val ROOT_URL = "http://10.237.188.134:3000"
//        private const val ROOT_URL = "http://172.20.10.1:3000"
const val BASE_URL = "${ROOT_URL}/api/"
    }

    private val retrofit = Retrofit.Builder()
        .addConverterFactory(GsonConverterFactory.create(GsonBuilder().create()))
        .baseUrl(BASE_URL)
        .build()

    private val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }

    val userRepository: UserRepository by lazy {
        UserRepository(userService)
    }
}
