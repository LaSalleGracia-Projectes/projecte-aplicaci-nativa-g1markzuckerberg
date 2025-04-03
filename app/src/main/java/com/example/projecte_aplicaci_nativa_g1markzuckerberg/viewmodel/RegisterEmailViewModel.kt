package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.AuthRepository
import kotlinx.coroutines.launch

class RegisterEmailViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _username = MutableLiveData("")
    val username: LiveData<String> = _username

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _confirmPassword = MutableLiveData("")
    val confirmPassword: LiveData<String> = _confirmPassword

    // Estados de carga y error
    private val _isLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun onUsernameChange(newUsername: String) {
        _username.value = newUsername
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
    }

    fun register(onSuccess: () -> Unit) {
        val username = _username.value ?: ""
        val email = _email.value ?: ""
        val password = _password.value ?: ""
        // Puedes agregar validaciones aquí
        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.register(username, email, password)
            _isLoading.value = false
            result.onSuccess {
                onSuccess()
            }.onFailure { error ->
                _errorMessage.value = error.message
            }
        }
    }
}

