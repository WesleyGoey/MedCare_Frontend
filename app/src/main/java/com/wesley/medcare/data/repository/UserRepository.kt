package com.wesley.medcare.data.repository

import android.content.Context
import com.wesley.medcare.data.dto.User.GetProfileResponse
import com.wesley.medcare.data.dto.User.LoginRequest
import com.wesley.medcare.data.dto.User.LoginResponse
import com.wesley.medcare.data.dto.User.RegisterRequest
import com.wesley.medcare.data.dto.User.RegisterResponse
import com.wesley.medcare.data.dto.User.UpdateUserRequest
import com.wesley.medcare.data.service.UserService
import retrofit2.Response
import androidx.core.content.edit

class UserRepository(
    private val context: Context,
    private val userService: UserService
) {
    private val prefs = context.getSharedPreferences("medcare_prefs", Context.MODE_PRIVATE)

    suspend fun login(email: String, password: String): Result<LoginResponse> {
        return try {
            println("Login attempt: $email")
            val response = userService.login(LoginRequest(email, password))
            println("Response code: ${response.code()}")
            println("Response body: ${response.body()}")

            if (response.isSuccessful && response.body() != null) {
                val loginResponse = response.body()!!
                saveToken(loginResponse.data.token)
                Result.success(loginResponse)
            } else {
                println("Login failed: ${response.errorBody()?.string()}")
                Result.failure(Exception("Login failed: ${response.message()}"))
            }
        } catch (e: Exception) {
            println("Login error: ${e.message}")
            Result.failure(e)
        }
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
        val response = userService.register(req)

        if (response.isSuccessful && response.body() != null) {
            val responseToken = response.body()!!.data.token
            saveToken(responseToken)
        }

        return response
    }

    suspend fun getProfile(): Response<GetProfileResponse> {
        val authToken = getToken() ?: throw Exception("No token found")
        return userService.getProfile("Bearer $authToken")
    }

    suspend fun updateProfile(request: UpdateUserRequest): Response<GetProfileResponse> {
        val authToken = getToken() ?: throw Exception("No token found")
        return userService.editProfile("Bearer $authToken", request)
    }

    private fun saveToken(token: String) {
        prefs.edit { putString("auth_token", token) }
    }

    fun getToken(): String? {
        return prefs.getString("auth_token", null)
    }

    fun clearToken() {
        prefs.edit {
            remove("auth_token")
        }
    }

    fun isLoggedIn(): Boolean {
        return !getToken().isNullOrBlank()
    }
}
