package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import androidx.lifecycle.*
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.AuthRepository
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.ContactRepository
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

    /* ---------- logout ---------- */
    fun logout(onSuccess: () -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.logoutMobile()
            _isLoading.value = false
            result
                .onSuccess { onSuccess() }
                .onFailure { _errorMessage.value = it.message }
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

    fun clearContactResult() { _contactResult.value = null }

}
