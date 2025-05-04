package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.*
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.Event
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class HomeLogedViewModel(application: Application) : AndroidViewModel(application) {

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
                    response.body()?.jornadaActual?.let { fetchJornada(it.name) }
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
                        val sorted = data.fixtures.sortedBy { it.starting_at_timestamp }
                        _fixturesState.value = sorted
                        _jornadaData.value = data.copy(fixtures = sorted)
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
                val resp = RetrofitClient.ligaService.joinLiga(code)
                if (resp.isSuccessful) {
                    fetchUserLeagues()
                } else {
                    val msg = when (resp.code()) {
                        404 -> getString(R.string.error_league_not_found)
                        409 -> getString(R.string.error_already_in_league)
                        422 -> getString(R.string.error_invalid_league_code)
                        403 -> getString(R.string.error_no_permission_join)
                        500 -> getString(R.string.error_server_try_later)
                        else -> getString(R.string.error_unknown_code, resp.code())
                    }
                    _errorMessage.value = Event(msg)
                }
            } catch (e: Exception) {
                _errorMessage.value = Event(
                    getString(R.string.error_network, e.message.orEmpty())
                )
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
                val resp = RetrofitClient.ligaService.createLiga(request)
                if (resp.isSuccessful) {
                    _createLigaResult.value = Event(resp.body())
                    val newId = resp.body()!!.liga.id.toString()
                    if (imageUri != null) {
                        updateLigaWithImage(newId, imageUri, context)
                    } else {
                        fetchUserLeagues()
                    }
                } else {
                    val msg = when (resp.code()) {
                        404 -> getString(R.string.error_cannot_create_league)
                        409 -> getString(R.string.error_league_exists)
                        422 -> getString(R.string.error_invalid_data)
                        403 -> getString(R.string.error_no_permission_create_league)
                        500 -> getString(R.string.error_server_create_league)
                        else -> getString(R.string.error_unknown_code, resp.code())
                    }
                    _errorMessage.value = Event(msg)
                }
            } catch (e: Exception) {
                _errorMessage.value = Event(
                    getString(R.string.error_creating_league, e.message.orEmpty())
                )
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateLigaWithImage(ligaId: String, imageUri: Uri, context: Context) {
        viewModelScope.launch {
            try {
                val mime = context.contentResolver.getType(imageUri) ?: return@launch
                if (mime !in listOf("image/png","image/jpg","image/jpeg")) {
                    _errorMessage.value =
                        Event(getString(R.string.error_image_format_not_allowed))
                    return@launch
                }
                val bytes = context.contentResolver.openInputStream(imageUri)!!.readBytes()
                val body = MultipartBody.Part.createFormData(
                    "image",
                    "leagueImage.${mime.substringAfter("/")}",
                    bytes.toRequestBody(mime.toMediaTypeOrNull())
                )
                val upResp = RetrofitClient.ligaService.uploadLeagueImage(ligaId, body)
                if (upResp.isSuccessful) {
                    fetchUserLeagues()
                    _lastImageUpdateTs.value = System.currentTimeMillis()
                } else {
                    _errorMessage.value =
                        Event(getString(R.string.error_upload_image_code, upResp.code()))
                }
            } catch (e: Exception) {
                _errorMessage.value =
                    Event(getString(R.string.error_upload_image_message, e.localizedMessage.orEmpty()))
            }
        }
    }

    fun fetchUserLeagues() {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.userService.getUserLeagues()
                if (resp.isSuccessful) {
                    _userLeagues.value = resp.body()?.leagues.orEmpty()
                } else {
                    _errorMessage.value =
                        Event(getString(R.string.error_loading_leagues_code, resp.code()))
                }
            } catch (e: Exception) {
                _errorMessage.value =
                    Event(getString(R.string.error_loading_leagues_message, e.message.orEmpty()))
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun leaveLiga(ligaId: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.ligaService.leaveLiga(ligaId)
                if (resp.isSuccessful) {
                    _leaveLigaResult.value = Event(getString(R.string.league_left))
                    fetchUserLeagues()
                } else {
                    _errorMessage.value =
                        Event(getString(R.string.error_cannot_leave_if_captain))
                }
            } catch (e: Exception) {
                _errorMessage.value =
                    Event(getString(R.string.error_leaving_league, e.localizedMessage.orEmpty()))
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun fetchUserInfo() {
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.userService.getMe()
                if (resp.isSuccessful) {
                    _userEmail.value = resp.body()?.user?.correo.orEmpty()
                } else if (resp.code() != 401) {
                    _errorMessage.value =
                        Event(getString(R.string.error_loading_user_code, resp.code()))
                }
            } catch (e: Exception) {
                _errorMessage.value =
                    Event(getString(R.string.error_loading_user_message, e.localizedMessage.orEmpty()))
            }
        }
    }

    fun updateLigaName(ligaId: String, newName: String) {
        _isLoading.value = true
        viewModelScope.launch {
            try {
                val request = UpdateLigaNameRequest(newName)
                val resp = RetrofitClient.ligaService.updateLigaName(ligaId, request)
                if (resp.isSuccessful) {
                    _updateLigaSuccess.value = Event(Unit)
                    fetchUserLeagues()
                } else {
                    _errorMessage.value =
                        Event(getString(R.string.error_updating_name_code, resp.code()))
                }
            } catch (e: Exception) {
                _errorMessage.value =
                    Event(getString(R.string.error_unexpected, e.localizedMessage.orEmpty()))
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Helper to avoid casting everywhere
    private fun getString(id: Int, vararg args: Any): String =
        getApplication<Application>().getString(id, *args)
}
