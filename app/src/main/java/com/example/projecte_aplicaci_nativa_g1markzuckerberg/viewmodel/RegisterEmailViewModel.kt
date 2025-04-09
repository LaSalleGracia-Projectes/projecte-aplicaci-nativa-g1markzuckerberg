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

    // NUEVAS VARIABLES PARA VALIDAR LA CONTRASEÑA
    private val _isMinLength = MutableLiveData(false)
    val isMinLength: LiveData<Boolean> = _isMinLength

    private val _hasUppercase = MutableLiveData(false)
    val hasUppercase: LiveData<Boolean> = _hasUppercase

    private val _hasDigit = MutableLiveData(false)
    val hasDigit: LiveData<Boolean> = _hasDigit

    private val _isPasswordValid = MutableLiveData(false)
    val isPasswordValid: LiveData<Boolean> = _isPasswordValid

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
        // Realizamos las validaciones:
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
        val username = _username.value ?: ""
        val email = _email.value ?: ""
        val password = _password.value ?: ""
        val confirmPassword = _confirmPassword.value ?: ""

        // Validamos que la contraseña y su confirmación sean iguales
        if (password != confirmPassword) {
            _errorMessage.value = "Las contraseñas no coinciden"
            return
        }

        // Validamos que la contraseña cumpla con todos los requisitos
        if (_isPasswordValid.value != true) {
            _errorMessage.value = "La contraseña no cumple con los requisitos (mínimo 6 caracteres, una mayúscula y números)"
            return
        }

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
