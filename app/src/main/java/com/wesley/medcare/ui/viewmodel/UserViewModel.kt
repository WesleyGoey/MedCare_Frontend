// kotlin
package com.wesley.medcare.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wesley.medcare.data.container.AppContainer
import com.wesley.medcare.ui.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class UserViewModel(application: Application) : AndroidViewModel(application) {

    // State for user data (token, profile, etc.)
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
                val response = repository.login(email, password)
                if (response.isSuccessful) {
                    val body = response.body()
                    val token = body?.`data`?.token
                    if (!token.isNullOrBlank()) {
                        // update user state to clear errors (adjust to your User model if it has token field)
                        _userState.value =
                            _userState.value.copy(isError = false, errorMessage = null)
                        // If your User model has a token field, set it here, e.g.
                        // _userState.value = _userState.value.copy(token = token, isError = false, errorMessage = null)
                    } else {
                        _userState.value = _userState.value.copy(
                            isError = true,
                            errorMessage = "Login failed: missing token"
                        )
                    }
                } else {
                    _userState.value = _userState.value.copy(
                        isError = true,
                        errorMessage = "Login failed: ${response.message()}"
                    )
                }
            } catch (e: IOException) {
                _userState.value = _userState.value.copy(
                    isError = true,
                    errorMessage = "Tidak ada koneksi internet."
                )
            } catch (e: Exception) {
                _userState.value = _userState.value.copy(
                    isError = true,
                    errorMessage = e.message ?: "Login gagal. Cek email/password."
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
                    // registration succeeded — update state accordingly
                    _userState.value = _userState.value.copy(isError = false, errorMessage = null)
                } else {
                    _userState.value = _userState.value.copy(
                        isError = true,
                        errorMessage = "Register failed: ${response.code()}"
                    )
                }
            } catch (e: IOException) {
                _userState.value = _userState.value.copy(
                    isError = true,
                    errorMessage = "Tidak ada koneksi internet."
                )
            } catch (e: Exception) {
                _userState.value = _userState.value.copy(
                    isError = true,
                    errorMessage = e.message ?: "Registrasi gagal."
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
                    val body = response.body()
                    val data = body?.`data`
                    if (data != null) {
                        // update userState with profile fields (adjust field names if your User model differs)
                        _userState.value = _userState.value.copy(
                            name = data.name,
                            email = data.email,
                            age = data.age,
                            phone = data.phone,
                            isError = false,
                            errorMessage = null
                        )
                    } else {
                        _userState.value = _userState.value.copy(
                            isError = true,
                            errorMessage = "Empty profile data"
                        )
                    }
                } else {
                    _userState.value = _userState.value.copy(
                        isError = true,
                        errorMessage = "Get profile failed: ${response.message()}"
                    )
                }
            } catch (e: IOException) {
                _userState.value = _userState.value.copy(
                    isError = true,
                    errorMessage = "Tidak ada koneksi internet."
                )
            } catch (e: Exception) {
                Log.e("UserViewModel", "getProfile error", e)
                _userState.value = _userState.value.copy(
                    isError = true,
                    errorMessage = e.message ?: "Gagal mengambil profil."
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Reset error flags/messages
    fun resetError() {
        _userState.value = _userState.value.copy(isError = false, errorMessage = null)
    }
}
