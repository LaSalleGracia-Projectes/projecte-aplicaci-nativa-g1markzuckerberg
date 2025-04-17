package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.AuthRepository
import kotlinx.coroutines.launch

class SettingsViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> get() = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> get() = _errorMessage

    fun logout(onSuccess: () -> Unit) {
        _isLoading.value = true
        viewModelScope.launch {
            val result = authRepository.logoutMobile()
            _isLoading.value = false
            result.onSuccess {
                onSuccess()
            }.onFailure { error ->
                _errorMessage.value = error.message
            }
        }
    }
}
