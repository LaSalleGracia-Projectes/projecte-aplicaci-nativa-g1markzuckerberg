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

    // Abandonar Liga
    private val _leaveLigaResult = MutableLiveData<Event<String>>()
    val leaveLigaResult: LiveData<Event<String>> = _leaveLigaResult

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail
    init {
        // Cargar la jornada actual al iniciar.
        fetchCurrentJornada()
        fetchUserInfo()
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

    fun joinLiga(code: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.ligaService.joinLiga(code)
                if (response.isSuccessful) {
                    fetchUserLeagues()
                } else {
                    // Aquí puedes verificar el código de error
                    when (response.code()) {
                        404 -> {
                            _errorMessage.value = Event("Error: Liga no encontrada.")
                        }
                        409 -> {
                            _errorMessage.value = Event("Error: Ya estás en esta liga.")
                        }
                        422 -> {
                            _errorMessage.value = Event("Error: Código de liga inválido.")
                        }
                        403 -> {
                            _errorMessage.value = Event("Error: No tienes permiso para unirte a esta liga.")
                        }
                        401 -> {
                            _errorMessage.value = Event("Error: No estás autenticado.")
                        }
                        500 -> {
                            _errorMessage.value = Event("Error del servidor. Intenta más tarde.")
                        }
                        else -> {
                            _errorMessage.value = Event("Error desconocido: ${response.code()}.")
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = Event("Error al conectarse con el servidor: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createLiga(name: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = CreateLigaRequest(name = name)
                val response = RetrofitClient.ligaService.createLiga(request)
                if (response.isSuccessful) {
                    fetchUserLeagues()
                } else {
                    // Mismo caso aquí
                    when (response.code()) {
                        404 -> {
                            _errorMessage.value = Event("Error: No se pudo crear la liga.")
                        }
                        409 -> {
                            _errorMessage.value = Event("Error: Liga ya existe.")
                        }
                        422 -> {
                            _errorMessage.value = Event("Error: Datos inválidos.")
                        }
                        403 -> {
                            _errorMessage.value = Event("Error: No tienes permiso para crear una liga.")
                        }
                        401 -> {
                            _errorMessage.value = Event("Error: No estás autenticado.")
                        }
                        500 -> {
                            _errorMessage.value = Event("Error del servidor al crear la liga. Intenta más tarde.")
                        }
                        else -> {
                            _errorMessage.value = Event("Error desconocido: ${response.code()}.")
                        }
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = Event("Error al conectar con el servidor: ${e.message}")
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
    fun leaveLiga(ligaId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val response = RetrofitClient.ligaService.leaveLiga(ligaId)
                if (response.isSuccessful) {
                    _leaveLigaResult.value = Event("Liga abandonada")
                    fetchUserLeagues() // Actualiza la lista de ligas
                } else {
                    _errorMessage.value = Event("El capitán no puede abandonar la liga")
                }
            } catch (e: Exception) {
                _errorMessage.value = Event(e.message ?: "Error desconocido")
            } finally {
                _isLoading.value = false
            }
        }
    }
    fun fetchUserInfo() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.userService.getMe() // Asegúrate de que este método exista
                if (response.isSuccessful) {
                    _userEmail.value = response.body()?.user?.correo ?: ""
                } else {
                    _errorMessage.value = Event("Error al obtener información de usuario: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = Event(e.message ?: "Error desconocido al obtener información del usuario")
            }
        }
    }
}
