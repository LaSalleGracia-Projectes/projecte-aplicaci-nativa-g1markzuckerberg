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
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.CreateLigaRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.CreateLigaResponse
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.Fixture
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.JoinLigaResponse
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.JornadaResponse
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.LigaConPuntos
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.UpdateLigaNameRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.Event
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class HomeLogedViewModel : ViewModel() {

    // Datos de la jornada y fixtures.
    private val _jornadaData = mutableStateOf<JornadaResponse?>(null)
    val jornadaData: State<JornadaResponse?> = _jornadaData

    private val _fixturesState = mutableStateOf<List<Fixture>>(emptyList())

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
    private val _isLoading = MutableLiveData(true)
    val isLoading: LiveData<Boolean> get() = _isLoading

    // Abandonar Liga
    private val _leaveLigaResult = MutableLiveData<Event<String>>()

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> = _userEmail

    private val _lastImageUpdateTs = MutableLiveData(System.currentTimeMillis())
    val lastImageUpdateTs: LiveData<Long> get() = _lastImageUpdateTs

    private val _updateLigaSuccess = MutableLiveData<Event<Unit>>()
    val updateLigaSuccess: LiveData<Event<Unit>> = _updateLigaSuccess

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

    fun createLiga(
        name: String,
        imageUri: Uri?,
        context: Context
    ) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                // 1) Creamos la liga
                val request = CreateLigaRequest(name = name.trim())
                val response = RetrofitClient.ligaService.createLiga(request)

                if (response.isSuccessful) {
                    // 2) Obtenemos el wrapper que trae message + liga
                    val wrapper = response.body()!!
                    _createLigaResult.value = Event(wrapper)

                    // 3) Extraemos el id de la nueva liga
                    val newLigaId = wrapper.liga.id.toString()

                    if (imageUri != null) {
                        // 4) Preparamos el multipart para la imagen
                        val mime = context.contentResolver.getType(imageUri)!!
                        val bytes = context.contentResolver.openInputStream(imageUri)!!.readBytes()
                        val reqFile = bytes.toRequestBody(mime.toMediaTypeOrNull())
                        val part = MultipartBody.Part.createFormData(
                            "image",
                            "leagueImage.${mime.substringAfter("/")}",
                            reqFile
                        )

                        // 5) Llamamos al endpoint de subida
                        val uploadResponse = RetrofitClient.ligaService.uploadLeagueImage(
                            newLigaId,
                            part
                        )

                        if (!uploadResponse.isSuccessful) {
                            Log.e("API_CALL",
                                "Error al subir imagen: ${uploadResponse.code()} " +
                                        uploadResponse.errorBody()?.string()
                            )
                        } else {
                            // 6) Si subió bien, recargamos lista y timestamp
                            fetchUserLeagues()
                            _lastImageUpdateTs.value = System.currentTimeMillis()
                        }
                    } else {
                        // Si no había imagen, simplemente recargamos la lista
                        fetchUserLeagues()
                    }
                } else {
                    // Manejo de errores HTTP al crear liga
                    when (response.code()) {
                        404 -> _errorMessage.value = Event("Error: No se pudo crear la liga.")
                        409 -> _errorMessage.value = Event("Error: Liga ya existe.")
                        422 -> _errorMessage.value = Event("Error: Datos inválidos.")
                        403 -> _errorMessage.value = Event("Error: No tienes permiso para crear una liga.")
                        401 -> _errorMessage.value = Event("Error: No estás autenticado.")
                        500 -> _errorMessage.value = Event("Error del servidor al crear la liga. Intenta más tarde.")
                        else -> _errorMessage.value = Event("Error desconocido: ${response.code()}.")
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
                    fetchUserLeagues()
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
    private fun fetchUserInfo() {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.userService.getMe()
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
    fun updateLigaWithImage(ligaId: String, imageUri: Uri, context: Context) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val contentResolver = context.contentResolver
                val mimeType = contentResolver.getType(imageUri) ?: ""

                if (mimeType !in listOf("image/png", "image/jpg", "image/jpeg")) {
                    _errorMessage.value = Event("Formato de imagen no permitido (solo png, jpg, jpeg).")
                    return@launch
                }

                val inputStream = contentResolver.openInputStream(imageUri)!!
                val bytes = inputStream.readBytes()
                val requestFile = bytes.toRequestBody(mimeType.toMediaTypeOrNull())
                val body = MultipartBody.Part.createFormData(
                    "image",
                    "leagueImage.${mimeType.substringAfter("/")}",
                    requestFile
                )

                val response = RetrofitClient.ligaService.uploadLeagueImage(ligaId, body)

                if (response.isSuccessful) {
                    _updateLigaSuccess.value = Event(Unit)
                    fetchUserLeagues() // Actualizamos la lista de ligas.
                    // Actualizamos el timestamp para forzar la recarga de las imágenes.
                    _lastImageUpdateTs.value = System.currentTimeMillis()
                } else {
                    _errorMessage.value = Event("Error al subir imagen: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = Event("Error inesperado: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateLigaName(ligaId: String, newName: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = UpdateLigaNameRequest(newName = newName)
                val response = RetrofitClient.ligaService.updateLigaName(ligaId, request)
                if (response.isSuccessful) {
                    _updateLigaSuccess.value = Event(Unit)
                    fetchUserLeagues()  // Refrescar lista y vista
                } else {
                    _errorMessage.value = Event("Error al actualizar nombre: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorMessage.value = Event("Error inesperado: ${e.localizedMessage}")
            } finally {
                _isLoading.value = false
            }
        }
    }

}
