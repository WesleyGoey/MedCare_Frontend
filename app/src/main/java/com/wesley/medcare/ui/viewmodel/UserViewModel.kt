package com.jeruk.alp_frontend.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.jeruk.alp_frontend.data.container.AppContainer
import com.jeruk.alp_frontend.ui.model.User
import com.wesley.medcare.data.container.AppContainer
import com.wesley.medcare.ui.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class UserViewModel : ViewModel() {

    // State for user data (token, profile, etc.)
    private val _userState = MutableStateFlow(User())
    val userState: StateFlow<User> = _userState

    // State for loading spinner
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val repository = AppContainer().userRepository

    // Login by email
    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.loginUser(email, pass) // adapt to your repo signature
                _userState.value = result
            } catch (e: IOException) {
                _userState.value = User(
                    isError = true,
                    errorMessage = "Tidak ada koneksi internet."
                )
            } catch (e: Exception) {
                _userState.value = User(
                    isError = true,
                    errorMessage = e.message ?: "Login gagal. Cek email/password."
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Register new user
    fun register(name: String, email: String, pass: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.register(name, email, pass, age, phone)
                _userState.value = result
            } catch (e: IOException) {
                _userState.value = User(
                    isError = true,
                    errorMessage = "Tidak ada koneksi internet."
                )
            } catch (e: Exception) {
                _userState.value = User(
                    isError = true,
                    errorMessage = e.message ?: "Registrasi gagal."
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Logout and clear user state
    fun logout() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                repository.logout()
                _userState.value = User()
            } catch (e: Exception) {
                _userState.value = _userState.value.copy(
                    isError = true,
                    errorMessage = e.message ?: "Logout gagal."
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
