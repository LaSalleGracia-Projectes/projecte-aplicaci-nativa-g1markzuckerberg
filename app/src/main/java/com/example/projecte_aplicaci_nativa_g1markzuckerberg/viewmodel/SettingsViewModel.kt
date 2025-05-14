package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import android.content.Context
import androidx.lifecycle.*
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.AuthRepository
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.ContactRepository
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val authRepository: AuthRepository,
    private val contactRepository: ContactRepository
) : ViewModel() {

    /* ---------- loading / error ---------- */
    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    /* ---------- tema oscuro (sólo RAM) ---------- */
    private val _isDarkTheme = MutableLiveData(false)
    val isDarkTheme: LiveData<Boolean> = _isDarkTheme

    fun toggleTheme() {
        _isDarkTheme.value = !(_isDarkTheme.value ?: false)
    }

    /* ---------- logout completo ---------- */
    fun logout(context: Context, onSuccess: () -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.logoutMobile()
            _isLoading.value = false
            result
                .onSuccess {
                    // 🔹 1. Cierra sesión de Google
                    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                        .requestEmail()
                        .build()
                    val googleSignInClient = GoogleSignIn.getClient(context, gso)
                    googleSignInClient.signOut()

                    // 🔹 2. Borra token FCM
                    FirebaseMessaging.getInstance().deleteToken()

                    onSuccess()
                }
                .onFailure { exception ->
                    _errorMessage.value = exception.message
                }
        }
    }

    // ---------- contacto ----------
    private val _contactResult = MutableLiveData<Result<Unit>?>(null)
    val contactResult: LiveData<Result<Unit>?> = _contactResult

    fun sendContactForm(message: String) {
        _isLoading.value = true
        viewModelScope.launch {
            _contactResult.value = contactRepository.sendContactForm(message)
            _isLoading.value = false
        }
    }

    fun clearContactResult() {
        _contactResult.value = null
    }
}
