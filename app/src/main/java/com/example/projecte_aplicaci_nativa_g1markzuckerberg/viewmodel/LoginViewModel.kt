package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import android.app.Application
import android.content.Context
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.ForgotPasswordRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.AuthRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import kotlinx.coroutines.launch

class LoginViewModel(
    application: Application,
    private val authRepository: AuthRepository
) : AndroidViewModel(application) {

    private val _email = MutableLiveData("")
    val email: LiveData<String> get() = _email

    private val _password = MutableLiveData("")
    val password: LiveData<String> get() = _password

    private val _passwordVisible = MutableLiveData(false)
    val passwordVisible: LiveData<Boolean> get() = _passwordVisible

    private val _forgotPasswordMessage = MutableLiveData<String>()
    val forgotPasswordMessage: LiveData<String> = _forgotPasswordMessage

    fun onEmailChanged(newEmail: String) {
        _email.value = newEmail
    }

    fun onPasswordChanged(newPassword: String) {
        _password.value = newPassword
    }

    fun togglePasswordVisibility() {
        _passwordVisible.value = _passwordVisible.value?.not() ?: false
    }

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun login(onSuccess: () -> Unit) {
        val emailValue = _email.value.orEmpty()
        val passwordValue = _password.value.orEmpty()

        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.login(emailValue, passwordValue)
            _isLoading.value = false
            result.onSuccess {
                onSuccess()
            }.onFailure { error ->
                val msg = getApplication<Application>()
                    .getString(R.string.login_error_generic, error.message.orEmpty())
                _errorMessage.value = msg
            }
        }
    }

    private lateinit var googleSignInClient: GoogleSignInClient

    fun initGoogle(context: Context) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken("884876575294-4t7eid7marm9u9s2u9mb7agv5i1k4lgu.apps.googleusercontent.com")
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    fun getGoogleSignInIntent(): Intent {
        return googleSignInClient.signInIntent
    }

    fun handleGoogleToken(idToken: String, onSuccess: () -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.loginWithGoogle(idToken)
            _isLoading.value = false
            result.onSuccess {
                onSuccess()
            }.onFailure { error ->
                val msg = getApplication<Application>()
                    .getString(R.string.login_error_generic, error.message.orEmpty())
                _errorMessage.value = msg
            }
        }
    }

    fun forgotPassword(email: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.userService
                    .forgotPassword(ForgotPasswordRequest(email))
                if (response.isSuccessful) {
                    _forgotPasswordMessage.value = response.body()?.message
                        ?: getApplication<Application>()
                            .getString(R.string.email_sent_success)
                } else {
                    _forgotPasswordMessage.value = getApplication<Application>()
                        .getString(R.string.forgot_password_error_code, response.code())
                }
            } catch (e: Exception) {
                _forgotPasswordMessage.value = getApplication<Application>()
                    .getString(R.string.forgot_password_error_network, e.message.orEmpty())
            }
        }
    }

    fun clearError() {
        _errorMessage.value = null
    }
}
