package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.AuthRepository
import kotlinx.coroutines.launch
import retrofit2.HttpException

class RegisterEmailViewModel(
    application: Application,
    private val authRepository: AuthRepository
) : AndroidViewModel(application) {

    private val _username = MutableLiveData("")
    val username: LiveData<String> = _username

    private val _email = MutableLiveData("")
    val email: LiveData<String> = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> = _password

    private val _confirmPassword = MutableLiveData("")
    val confirmPassword: LiveData<String> = _confirmPassword

    // Validaciones de contraseña
    private val _isMinLength = MutableLiveData(false)
    val isMinLength: LiveData<Boolean> = _isMinLength

    private val _hasUppercase = MutableLiveData(false)
    val hasUppercase: LiveData<Boolean> = _hasUppercase

    private val _hasDigit = MutableLiveData(false)
    val hasDigit: LiveData<Boolean> = _hasDigit

    private val _isPasswordValid = MutableLiveData(false)

    // Estados de UI
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _successMessage = MutableLiveData<String?>()
    val successMessage: LiveData<String?> = _successMessage

    fun clearError() {
        _errorMessage.value = null
    }

    fun onUsernameChange(newUsername: String) {
        _username.value = newUsername
    }

    fun onEmailChange(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChange(newPassword: String) {
        _password.value = newPassword
        // Validamos la contraseña
        val minLength = newPassword.length >= 6
        val uppercase = newPassword.any { it.isUpperCase() }
        val digit = newPassword.any { it.isDigit() }
        _isMinLength.value = minLength
        _hasUppercase.value = uppercase
        _hasDigit.value = digit
        _isPasswordValid.value = minLength && uppercase && digit
    }

    fun onConfirmPasswordChange(newConfirmPassword: String) {
        _confirmPassword.value = newConfirmPassword
    }

    fun register(onSuccess: () -> Unit) {
        val username = _username.value.orEmpty()
        val email = _email.value.orEmpty()
        val password = _password.value.orEmpty()
        val confirmPassword = _confirmPassword.value.orEmpty()

        // Contraseñas coincidentes
        if (password != confirmPassword) {
            _errorMessage.value = getApplication<Application>()
                .getString(R.string.passwords_mismatch)
            return
        }
        // Requisitos de contraseña
        if (_isPasswordValid.value != true) {
            _errorMessage.value = getApplication<Application>()
                .getString(R.string.password_requirements)
            return
        }

        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.register(username, email, password)
            _isLoading.value = false

            result.onSuccess {
                _successMessage.value = getApplication<Application>()
                    .getString(R.string.account_registered_success)
                onSuccess()
            }.onFailure { error ->
                val msg = if (error is HttpException) {
                    when (error.code()) {
                        400 -> getApplication<Application>()
                            .getString(R.string.invalid_data)
                        409 -> getApplication<Application>()
                            .getString(R.string.user_or_email_exists)
                        else -> getApplication<Application>()
                            .getString(
                                R.string.account_creation_error_code,
                                error.code()
                            )
                    }
                } else {
                    // utiliza placeholder genérico
                    getApplication<Application>().getString(
                        R.string.account_creation_error_generic,
                        error.message.orEmpty()
                    )
                }
                _errorMessage.value = msg
            }
        }
    }
}
