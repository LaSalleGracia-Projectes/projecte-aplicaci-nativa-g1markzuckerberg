package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient.authRepository
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.CreateLigaRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.CreateLigaResponse
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.Fixture
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.JoinLigaResponse
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.JornadaResponse
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.LigaConPuntos
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.AuthRepository
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.Event
import kotlinx.coroutines.launch

class HomeLogedViewModel(private val authRepository: AuthRepository) : ViewModel() {

    // Datos de la jornada y fixtures.
    private val _jornadaData = mutableStateOf<JornadaResponse?>(null)
    val jornadaData: State<JornadaResponse?> = _jornadaData

    private val _fixturesState = mutableStateOf<List<Fixture>>(emptyList())
    val fixturesState: State<List<Fixture>> = _fixturesState

    // Evento para la creación de liga.
    private val _createLigaResult = MutableLiveData<Event<CreateLigaResponse?>>()
    val createLigaResult: LiveData<Event<CreateLigaResponse?>> = _createLigaResult

    // Evento para mensajes de error.
    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    // Evento para la unión a una liga.
    private val _joinLigaResult = MutableLiveData<Event<JoinLigaResponse?>>()
    val joinLigaResult: LiveData<Event<JoinLigaResponse?>> = _joinLigaResult

    // Lista de ligas del usuario.
    private val _userLeagues = MutableLiveData<List<LigaConPuntos>>()
    val userLeagues: LiveData<List<LigaConPuntos>> = _userLeagues

    // Estado de carga.
    private val _isLoading = MutableLiveData<Boolean>(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    init {
        // Cargar la jornada actual al iniciar.
        fetchCurrentJornada()
    }

    private fun fetchCurrentJornada() {
        viewModelScope.launch {
            try {
                val currentResponse = RetrofitClient.service.getJornadaActual()
                if (currentResponse.isSuccessful) {
                    val currentJornada = currentResponse.body()?.jornadaActual
                    if (currentJornada != null) {
                        // Llamamos a fetchJornada con el valor recibido.
                        fetchJornada(currentJornada.name)
                    } else {
                        Log.e("API_CALL", "No se recibió la jornada actual")
                    }
                } else {
                    Log.e("API_CALL", "Error en getJornadaActual: ${currentResponse.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_CALL", "Excepción en getJornadaActual: ${e.message}")
            }
        }
    }

    private fun fetchJornada(jornada: String) {
        viewModelScope.launch {
            try {
                Log.d("API_CALL", "Realizando petición para jornada: $jornada")
                val response = RetrofitClient.service.getJornada(jornada)
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        val sortedFixtures = data.fixtures.sortedBy { it.starting_at_timestamp }
                        _fixturesState.value = sortedFixtures
                        _jornadaData.value = data.copy(fixtures = sortedFixtures)
                    }
                } else {
                    Log.e("API_CALL", "Error en respuesta: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_CALL", "Excepción: ${e.message}")
            }
        }
    }

    fun createLiga(name: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                val request = CreateLigaRequest(name = name)
                val response = RetrofitClient.ligaService.createLiga(request)
                if (response.isSuccessful) {
                    _createLigaResult.value = Event(response.body())
                } else {
                    _errorMessage.value = Event("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = Event(e.message ?: "Error desconocido")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun joinLiga(code: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.ligaService.joinLiga(code)
                if (response.isSuccessful) {
                    _joinLigaResult.value = Event(response.body())
                } else {
                    _errorMessage.value = Event("Error: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = Event(e.message ?: "Error desconocido")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun fetchUserLeagues() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.userService.getUserLeagues()
                if (response.isSuccessful) {
                    _userLeagues.value = response.body()?.leagues ?: emptyList()
                } else {
                    _errorMessage.value = Event("Error al cargar ligas: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = Event(e.message ?: "Error desconocido")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
