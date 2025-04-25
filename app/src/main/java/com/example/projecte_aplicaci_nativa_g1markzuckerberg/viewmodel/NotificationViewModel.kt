package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.interface_service.NotificationsService
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.Notifications
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

/* ---------- Estado de la UI ---------- */
sealed interface NotificationsUiState {
    object Loading : NotificationsUiState
    data class Success(val data: List<Notifications>) : NotificationsUiState
    data class Error(val msg: String) : NotificationsUiState
}

/* ---------- “Repositorio” ultra-ligero ---------- */
/*  ➜  Le damos un valor por defecto que apunta al service de RetrofitClient,
        así evitamos el error “No value passed for parameter 'service'”. */
class NotificationsRepository(
    private val service: NotificationsService = RetrofitClient.notificationsService
) {
    suspend fun fetch(): List<Notifications> {
        val response = service.getUserNotifications()
        if (response.isSuccessful) {
            return response.body()?.notifications ?: emptyList()
        } else {
            throw HttpException(response)
        }
    }
}

/* ---------- ViewModel ---------- */
class NotificationViewModel(
    private val repository: NotificationsRepository = NotificationsRepository()
) : ViewModel() {

    private val _uiState = MutableStateFlow<NotificationsUiState>(NotificationsUiState.Loading)
    val uiState: StateFlow<NotificationsUiState> = _uiState

    init { loadNotifications() }

    fun loadNotifications() = viewModelScope.launch {
        _uiState.value = NotificationsUiState.Loading
        try {
            val data = repository.fetch()
            _uiState.value = NotificationsUiState.Success(data)
        } catch (_: IOException) {
            _uiState.value = NotificationsUiState.Error("Sin conexión")
        } catch (e: Exception) {
            _uiState.value = NotificationsUiState.Error(e.localizedMessage ?: "Error inesperado")
        }
    }
}
