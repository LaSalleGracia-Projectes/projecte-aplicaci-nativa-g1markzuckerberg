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
import kotlinx.coroutines.launch
import retrofit2.Response

class HomeLogedViewModel(private val authRepository: AuthRepository) : ViewModel() {

    private val _jornadaData = mutableStateOf<JornadaResponse?>(null)
    val jornadaData: State<JornadaResponse?> = _jornadaData

    private val _fixturesState = mutableStateOf<List<Fixture>>(emptyList())
    val fixturesState: State<List<Fixture>> = _fixturesState

    private val _createLigaResult = MutableLiveData<CreateLigaResponse?>()
    val createLigaResult: LiveData<CreateLigaResponse?> = _createLigaResult

    private val _errorMessage = MutableLiveData<String>("")
    val errorMessage: LiveData<String> = _errorMessage

    private val _joinLigaResult = MutableLiveData<JoinLigaResponse?>()
    val joinLigaResult: LiveData<JoinLigaResponse?> = _joinLigaResult

    private val _userLeagues = MutableLiveData<List<LigaConPuntos>>()
    val userLeagues: LiveData<List<LigaConPuntos>> = _userLeagues

    init {
        // Cargar por defecto la jornada 28
        fetchCurrentJornada()
    }

    private fun fetchCurrentJornada() {
        viewModelScope.launch {
            try {
                // Primero obtenemos la jornada actual
                val currentResponse = RetrofitClient.service.getJornadaActual()
                if (currentResponse.isSuccessful) {
                    val currentJornada = currentResponse.body()?.jornadaActual
                    if (currentJornada != null) {
                        Log.d("API_CALL", "Jornada actual recibida: ${currentJornada.name}")
                        // Usamos el valor recibido (ej. "30") para obtener los fixtures
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
                        // Ordenamos los fixtures por starting_at_timestamp de forma ascendente
                        val sortedFixtures = data.fixtures.sortedBy { it.starting_at_timestamp }
                        _fixturesState.value = sortedFixtures
                        // Actualizamos _jornadaData, asegurándonos de incluir los fixtures ordenados
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
                val request = CreateLigaRequest(name = name)
                val response = RetrofitClient.ligaService.createLiga(request)
                if (response.isSuccessful) {
                    _createLigaResult.value = response.body()
                } else {
                    _errorMessage.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
            }
        }
    }
    fun joinLiga(code: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.ligaService.joinLiga(code)
                if (response.isSuccessful) {
                    _joinLigaResult.value = response.body()
                } else {
                    _errorMessage.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
            }
        }
    }
    fun fetchUserLeagues() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.userService.getUserLeagues()
                if (response.isSuccessful) {
                    _userLeagues.value = response.body()?.leagues ?: emptyList()
                } else {
                    _errorMessage.value = "Error al cargar ligas: ${response.code()}"
                }
            } catch (e: Exception) {
                _errorMessage.value = e.message ?: "Error desconocido"
            }
        }
    }


}
