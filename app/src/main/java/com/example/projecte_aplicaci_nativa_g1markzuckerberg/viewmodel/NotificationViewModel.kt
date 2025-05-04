package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.interface_service.NotificationsService
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.Notifications
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface NotificationsUiState {
    object Loading : NotificationsUiState
    data class Success(val data: List<Notifications>) : NotificationsUiState
    data class Error(val msg: String) : NotificationsUiState
}

class NotificationsRepository(
    private val service: NotificationsService = RetrofitClient.notificationsService
) {
    suspend fun fetch(): List<Notifications> {
        val resp = service.getUserNotifications()
        if (resp.isSuccessful) return resp.body()?.notifications ?: emptyList()
        throw HttpException(resp)
    }
}

class NotificationViewModel(
    private val repo: NotificationsRepository = NotificationsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotificationsUiState>(NotificationsUiState.Loading)
    val uiState: StateFlow<NotificationsUiState> = _uiState

    fun forceReloadIfTokenExists() {
        if (RetrofitClient.authRepository.getToken().isNullOrEmpty()) return
        viewModelScope.launch { fetchWithRetry() }
    }

    private suspend fun fetchWithRetry(maxRetries: Int = 2) {
        _uiState.value = NotificationsUiState.Loading
        repeat(maxRetries + 1) { attempt ->
            try {
                _uiState.value = NotificationsUiState.Success(repo.fetch())
                return
            } catch (e: HttpException) {
                if (e.code() == 401) {
                    _uiState.value = NotificationsUiState.Success(emptyList())
                    return
                }
                if (attempt < maxRetries) {
                    delay(500)
                    return@repeat
                }
                _uiState.value = NotificationsUiState.Error("Error ${e.code()}")
                return
            } catch (_: IOException) {
                _uiState.value = NotificationsUiState.Error("Sin conexión")
                return
            } catch (e: Exception) {
                _uiState.value = NotificationsUiState.Error(e.localizedMessage ?: "Error")
                return
            }
        }
    }
}
