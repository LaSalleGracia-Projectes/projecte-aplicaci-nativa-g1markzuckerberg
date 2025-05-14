package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import androidx.lifecycle.*
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.LigaUsersResponse
import kotlinx.coroutines.launch

class LigaViewModel : ViewModel() {

    private val _ligaData = MutableLiveData<LigaUsersResponse>()
    val ligaData: LiveData<LigaUsersResponse> = _ligaData

    // Se usará para guardar la jornada seleccionada; el valor 0 se interpretará como "Total"
    private val _selectedJornada = MutableLiveData<Int>(0)
    val selectedJornada: LiveData<Int> = _selectedJornada

    // Jornada actual (obtenida del endpoint de SportMonks)
    private val _currentJornada = MutableLiveData<Int>()
    val currentJornada: LiveData<Int> = _currentJornada

    private val _showCodeDialog = MutableLiveData(false)
    val showCodeDialog: LiveData<Boolean> = _showCodeDialog

    // NUEVO: Estado de carga
    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        // Llamamos a obtener la jornada actual desde el endpoint
        fetchCurrentJornada()
    }

    fun fetchLigaInfo(ligaCode: String, jornada: Int? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = RetrofitClient.ligaService.getUsersByLiga(ligaCode, jornada)
                if (response.isSuccessful) {
                    response.body()?.let { body ->
                        val usersOrdenados = when (jornada) {
                            null, 0 -> body.users.sortedByDescending { it.puntos_acumulados }
                            else    -> body.users.sortedByDescending { it.puntos_jornada }
                        }
                        _ligaData.postValue(body.copy(users = usersOrdenados))
                    }
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchCurrentJornada() {
        viewModelScope.launch {
            val response = RetrofitClient.service.getJornadaActual()
            if (response.isSuccessful) {
                // Suponemos que jornadaActual.name es un número en formato String
                val current = response.body()?.jornadaActual?.name?.toIntOrNull() ?: 30
                _currentJornada.postValue(current)
            }
        }
    }

    fun toggleShowCodeDialog() {
        _showCodeDialog.value = _showCodeDialog.value != true
    }

    fun setSelectedJornada(jornada: Int) {
        _selectedJornada.value = jornada
    }
}
