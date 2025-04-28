package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.*
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.Event
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class HomeLogedViewModel : ViewModel() {

    private val _jornadaData = mutableStateOf<JornadaResponse?>(null)
    val jornadaData: State<JornadaResponse?> = _jornadaData

    private val _fixturesState = mutableStateOf<List<Fixture>>(emptyList())
    val fixturesState: State<List<Fixture>> = _fixturesState

    private val _createLigaResult = MutableLiveData<Event<CreateLigaResponse?>>()
    val createLigaResult: LiveData<Event<CreateLigaResponse?>> = _createLigaResult

    private val _errorMessage = MutableLiveData<Event<String>>()
    val errorMessage: LiveData<Event<String>> = _errorMessage

    private val _joinLigaResult = MutableLiveData<Event<JoinLigaResponse?>>()
    val joinLigaResult: LiveData<Event<JoinLigaResponse?>> = _joinLigaResult

    private val _userLeagues = MutableLiveData<List<LigaConPuntos>>()
    val userLeagues: LiveData<List<LigaConPuntos>> = _userLeagues

    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> = _isLoading

    private val _leaveLigaResult = MutableLiveData<Event<String>>()
    val leaveLigaResult: LiveData<Event<String>> = _leaveLigaResult

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail

    private val _lastImageUpdateTs = MutableLiveData(System.currentTimeMillis())
    val lastImageUpdateTs: LiveData<Long> = _lastImageUpdateTs

    private val _updateLigaSuccess = MutableLiveData<Event<Unit>>()
    val updateLigaSuccess: LiveData<Event<Unit>> = _updateLigaSuccess

    init {
        fetchCurrentJornada()
        fetchUserInfo()
    }

    private fun fetchCurrentJornada() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.service.getJornadaActual()
                if (response.isSuccessful) {
                    val jornada = response.body()?.jornadaActual
                    jornada?.let { fetchJornada(it.name) }
                } else {
                    Log.e("API_CALL", "Error en getJornadaActual: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_CALL", "Excepción en getJornadaActual: ${e.message}")
            }
        }
    }

    private fun fetchJornada(jornada: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.service.getJornada(jornada)
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        val sortedFixtures = data.fixtures.sortedBy { it.starting_at_timestamp }
                        _fixturesState.value = sortedFixtures
                        _jornadaData.value = data.copy(fixtures = sortedFixtures)
                    }
                } else {
                    Log.e("API_CALL", "Error en respuesta jornada: ${response.code()}")
                }
            } catch (e: Exception) {
                Log.e("API_CALL", "Excepción jornada: ${e.message}")
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
                    when (response.code()) {
                        404 -> _errorMessage.value = Event("Error: Liga no encontrada.")
                        409 -> _errorMessage.value = Event("Error: Ya estás en esta liga.")
                        422 -> _errorMessage.value = Event("Error: Código de liga inválido.")
                        403 -> _errorMessage.value = Event("Error: No tienes permiso para unirte.")
                        401 -> {} // ignoramos para evitar alertas en el primer arranque
                        500 -> _errorMessage.value = Event("Error del servidor. Intenta más tarde.")
                        else -> _errorMessage.value = Event("Error desconocido: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = Event("Error de red: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun createLiga(name: String, imageUri: Uri?, context: Context) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = CreateLigaRequest(name.trim())
                val response = RetrofitClient.ligaService.createLiga(request)
                if (response.isSuccessful) {
                    val wrapper = response.body()!!
                    _createLigaResult.value = Event(wrapper)
                    val newLigaId = wrapper.liga.id.toString()

                    if (imageUri != null) {
                        uploadLigaImage(newLigaId, imageUri, context)
                    } else {
                        fetchUserLeagues()
                    }
                } else {
                    when (response.code()) {
                        404 -> _errorMessage.value = Event("Error: No se pudo crear la liga.")
                        409 -> _errorMessage.value = Event("Error: Liga ya existe.")
                        422 -> _errorMessage.value = Event("Error: Datos inválidos.")
                        403 -> _errorMessage.value = Event("Error: No tienes permiso para crear liga.")
                        401 -> {} // ignorado
                        500 -> _errorMessage.value = Event("Error del servidor al crear la liga.")
                        else -> _errorMessage.value = Event("Error desconocido: ${response.code()}")
                    }
                }
            } catch (e: Exception) {
                _errorMessage.value = Event("Error creando liga: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun uploadLigaImage(ligaId: String, imageUri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                val contentResolver = context.contentResolver
                val mimeType = contentResolver.getType(imageUri) ?: return@launch
                if (mimeType !in listOf("image/png", "image/jpg", "image/jpeg")) {
                    _errorMessage.value = Event("Formato de imagen no permitido.")
                    return@launch
                }

                val bytes = contentResolver.openInputStream(imageUri)!!.readBytes()
                val requestFile = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData(
                    "image",
                    "leagueImage.${mimeType.substringAfter("/")}",
                    requestFile
                )

                val uploadResponse = RetrofitClient.ligaService.uploadLeagueImage(ligaId, body)
                if (uploadResponse.isSuccessful) {
                    fetchUserLeagues()
                    _lastImageUpdateTs.value = System.currentTimeMillis()
                } else {
                    _errorMessage.value = Event("Error al subir imagen: ${uploadResponse.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = Event("Error al subir imagen: ${e.localizedMessage}")
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
                    _errorMessage.value = Event("Error cargando ligas: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = Event("Error cargando ligas: ${e.message}")
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
                    fetchUserLeagues()
                } else {
                    _errorMessage.value = Event("No puedes abandonar si eres el capitán.")
                }
            } catch (e: Exception) {
                _errorMessage.value = Event("Error abandonando liga: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchUserInfo() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.userService.getMe()
                if (response.isSuccessful) {
                    _userEmail.value = response.body()?.user?.correo ?: ""
                } else if (response.code() != 401) {
                    _errorMessage.value = Event("Error al cargar usuario: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = Event("Error cargando usuario: ${e.localizedMessage}")
            }
        }
    }

    fun updateLigaName(ligaId: String, newName: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = UpdateLigaNameRequest(newName)
                val response = RetrofitClient.ligaService.updateLigaName(ligaId, request)
                if (response.isSuccessful) {
                    _updateLigaSuccess.value = Event(Unit)
                    fetchUserLeagues()
                } else {
                    _errorMessage.value = Event("Error actualizando nombre: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = Event("Error inesperado: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }
}
