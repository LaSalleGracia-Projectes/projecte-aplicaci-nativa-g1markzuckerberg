package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.JornadaActual
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.LigaUsersResponse
import kotlinx.coroutines.launch

class LigaViewModel : ViewModel() {

    private val _ligaData = MutableLiveData<LigaUsersResponse>()
    val ligaData: LiveData<LigaUsersResponse> = _ligaData

    private val _selectedJornada = MutableLiveData(0)
    val selectedJornada: LiveData<Int> = _selectedJornada

    private val _currentJornada = MutableLiveData(0)
    val currentJornada: LiveData<Int> = _currentJornada

    private val _currentJornadaActual = MutableLiveData<JornadaActual>()
    val currentJornadaActual: LiveData<JornadaActual> = _currentJornadaActual

    private val _showCodeDialog = MutableLiveData(false)
    val showCodeDialog: LiveData<Boolean> = _showCodeDialog

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        fetchCurrentJornada()
    }

    private fun fetchCurrentJornada() {
        viewModelScope.launch {
            val response = RetrofitClient.service.getJornadaActual()
            if (response.isSuccessful) {
                response.body()?.jornadaActual?.let { ja ->
                    _currentJornadaActual.postValue(ja)
                    _currentJornada.postValue(ja.name.toIntOrNull() ?: 0)
                }
            }
        }
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

    fun setSelectedJornada(jornada: Int) {
        _selectedJornada.value = jornada
        // fetchLigaInfo se debe llamar desde el Composable para tener el ligaCode correcto
    }

    fun toggleShowCodeDialog() {
        _showCodeDialog.value = _showCodeDialog.value != true
    }
}
