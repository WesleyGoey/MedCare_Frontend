package com.wesley.medcare.data.repository

import com.wesley.medcare.data.dto.User.LoginRequest
import com.wesley.medcare.data.dto.User.RegisterRequest
import com.wesley.medcare.data.service.AuthenticationAPIService
import com.wesley.medcare.ui.model.User

interface AuthenticationRepositoryInterface {
    suspend fun registerUser(username: String, email: String, password: String, age: Int, phone: String): User
    suspend fun loginUser(email: String, password: String): User
}

class AuthenticationRepository(
    private val authenticationAPIService: AuthenticationAPIService
) : AuthenticationRepositoryInterface {

    override suspend fun registerUser(username: String, email: String, password: String, age: Int, phone: String): User {
        return try {
            val request = RegisterRequest(name = username, email = email, password = password, age = age, phone = phone)
            val response = authenticationAPIService.register(request)

            if (response.isSuccessful) {
                response.body() ?: User(isError = true, errorMessage = "Registration failed")
            } else {
                User(isError = true, errorMessage = response.message() ?: "Unknown error")
            }
        } catch (e: Exception) {
            User(isError = true, errorMessage = e.message ?: "Network error")
        }
    }

    override suspend fun loginUser(email: String, password: String): User {
        return try {
            val request = LoginRequest(email = email, password = password)
            val response = authenticationAPIService.login(request)

            if (response.isSuccessful) {
                response.body() ?: User(isError = true, errorMessage = "Login failed")
            } else {
                User(isError = true, errorMessage = response.message() ?: "Invalid credentials")
            }
        } catch (e: Exception) {
            User(isError = true, errorMessage = e.message ?: "Network error")
        }
    }
}
