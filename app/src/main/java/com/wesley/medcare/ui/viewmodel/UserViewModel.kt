package com.wesley.medcare.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.wesley.medcare.data.container.AppContainer
import com.wesley.medcare.ui.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException

class UserViewModel : ViewModel() {

    private val repository = AppContainer().userRepository

    private val _userState = MutableStateFlow(User())
    val userState: StateFlow<User> = _userState

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.login(email, password)
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

    fun register(name: String, email: String, password: String, age: Int, phone: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val result = repository.register(name, email, password, age, phone)
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

    fun resetError() {
        _userState.value = _userState.value.copy(isError = false, errorMessage = null)
    }
}
