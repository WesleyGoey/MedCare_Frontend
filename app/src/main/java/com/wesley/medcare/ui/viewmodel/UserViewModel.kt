package com.wesley.medcare.ui.viewmodel

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.wesley.medcare.data.container.AppContainer
import com.wesley.medcare.data.dto.User.UpdateUserRequest
import com.wesley.medcare.ui.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class UserViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppContainer(application).userRepository

    private val _userState = MutableStateFlow(User())
    val userState: StateFlow<User> = _userState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    // Login by email
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.login(email, password)
                result.onSuccess { loginResponse ->
                    val token = loginResponse.data.token
                    if (!token.isNullOrBlank()) {
                        // Success: Clear any previous error
                        _userState.value = _userState.value.copy(
                            isError = false,
                            errorMessage = null
                        )
                    } else {
                        _userState.value = _userState.value.copy(
                            isError = true,
                            errorMessage = "Login failed: Token not found"
                        )
                    }
                }.onFailure { exception ->
                    _userState.value = _userState.value.copy(
                        isError = true,
                        errorMessage = exception.message ?: "Login failed. Please check your email and password."
                    )
                }
            } catch (e: Exception) {
                _userState.value = _userState.value.copy(
                    isError = true,
                    errorMessage = e.message ?: "An unexpected error occurred during login."
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Register new user
    fun register(name: String, age: Int, phone: String, email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.register(name, age, phone, email, password)
                if (response.isSuccessful) {
                    _userState.value = _userState.value.copy(
                        isError = false,
                        errorMessage = "Registration successful"
                    )
                } else {
                    _userState.value = _userState.value.copy(
                        isError = true,
                        errorMessage = "Registration failed. Status code: ${response.code()}"
                    )
                }
            } catch (e: IOException) {
                _userState.value = _userState.value.copy(
                    isError = true,
                    errorMessage = "No internet connection. Please check your network."
                )
            } catch (e: Exception) {
                _userState.value = _userState.value.copy(
                    isError = true,
                    errorMessage = e.message ?: "Registration failed due to an unknown error."
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Get profile of logged in user
    fun getProfile() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = repository.getProfile()
                if (response.isSuccessful) {
                    val data = response.body()?.data
                    if (data != null) {
                        _userState.value = _userState.value.copy(
                            name = data.name,
                            email = data.email,
                            age = data.age,
                            phone = data.phone,
                            isError = false,
                            errorMessage = null
                        )
                    }
                } else {
                    _userState.value = _userState.value.copy(
                        isError = true,
                        errorMessage = "Failed to load profile. Error code: ${response.code()}"
                    )
                }
            } catch (e: Exception) {
                _userState.value = _userState.value.copy(
                    isError = true,
                    errorMessage = e.message ?: "Network connection problem."
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Update user profile
    fun updateProfile(
        name: String,
        age: Int,
        phone: String,
        currentPassword: String?,
        newPassword: String?
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val request = UpdateUserRequest(
                    name = name,
                    age = age,
                    phone = phone,
                    currentPassword = currentPassword,
                    newPassword = newPassword
                )

                val response = repository.updateProfile(request)

                if (response.isSuccessful) {
                    // Refresh profile data to keep UI synced
                    getProfile()
                    _userState.value = _userState.value.copy(
                        isError = false,
                        errorMessage = "Profile updated successfully"
                    )
                } else {
                    val errorMsg = response.errorBody()?.string() ?: ""
                    val displayError = if (errorMsg.contains("password", ignoreCase = true)) {
                        "Current password is incorrect"
                    } else {
                        "Failed to update profile. Please try again."
                    }
                    _userState.value = _userState.value.copy(
                        isError = true,
                        errorMessage = displayError
                    )
                }
            } catch (e: Exception) {
                _userState.value = _userState.value.copy(
                    isError = true,
                    errorMessage = e.message ?: "System error occurred during update."
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Logout user
    fun logout(context: Context) {
        try {
            repository.clearToken()
            val sharedPref = context.getSharedPreferences("medcare_prefs", Context.MODE_PRIVATE)
            sharedPref.edit().clear().apply()

            // Reset UI state
            _userState.value = User()
            Log.d("UserViewModel", "Logout successful")
        } catch (e: Exception) {
            Log.e("UserViewModel", "Error during logout: ${e.message}")
        }
    }

    // Clear error state (called from UI after showing Toast/Snackbar)
    fun resetError() {
        _userState.value = _userState.value.copy(isError = false, errorMessage = null)
    }
}