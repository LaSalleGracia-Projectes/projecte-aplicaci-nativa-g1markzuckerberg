package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class DraftViewModel(application: Application) : AndroidViewModel(application) {

    private val _tempDraft = MutableLiveData<TempPlantillaResponse?>()
    val tempDraft: LiveData<TempPlantillaResponse?> = _tempDraft

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    private val _selectedFormation = mutableStateOf("4-3-3")
    val selectedFormation: State<String> = _selectedFormation

    private val _isFetchingDraft = MutableLiveData(false)
    val isFetchingDraft: LiveData<Boolean> = _isFetchingDraft

    private val _isSavingDraft = MutableLiveData(false)
    val isSavingDraft: LiveData<Boolean> = _isSavingDraft

    var currentLigaId: Int = 0
        private set

    fun setSelectedFormation(formation: String) {
        _selectedFormation.value = formation
    }

    fun clearError() {
        _errorMessage.value = ""
    }

    fun createAndFetchDraft(
        formation: String,
        ligaId: Int,
        onSuccess: () -> Unit
    ) {
        _isFetchingDraft.value = true
        viewModelScope.launch {
            // 1) Intentar POST, detectar 500
            try {
                val createResp = RetrofitClient.draftService.createDraft(
                    CreateDraftRequest(formation, ligaId)
                )
                if (createResp.code() == 500) {
                    _errorMessage.value = getString(R.string.error_draft_already_created)
                    _isFetchingDraft.value = false
                    return@launch
                }
            } catch (ex: Exception) {
                Log.w("DraftViewModel", "POST draft fallo inesperado", ex)
            }

            // 2) GET del draft
            try {
                val fetchResp = RetrofitClient.draftService.getTempDraft(ligaId)
                if (fetchResp.isSuccessful) {
                    _tempDraft.value = fetchResp.body()!!.tempDraft
                    currentLigaId = ligaId
                    onSuccess()
                } else {
                    _errorMessage.value = getString(
                        R.string.error_fetch_draft_code,
                        fetchResp.code()
                    )
                }
            } catch (ex: Exception) {
                _errorMessage.value = getString(
                    R.string.error_fetch_draft_network,
                    ex.message.orEmpty()
                )
                Log.e("DraftViewModel", "GET tempDraft excepción", ex)
            } finally {
                _isFetchingDraft.value = false
            }
        }
    }

    fun createOrFetchDraft(
        ligaId: Int,
        onSuccess: () -> Unit,
        onRequestFormation: () -> Unit
    ) {
        _isFetchingDraft.value = true
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.draftService.getTempDraft(ligaId)
                Log.d("DraftViewModel", "GET draft: code ${resp.code()} body ${resp.body()}")
                if (resp.isSuccessful) {
                    _tempDraft.value = resp.body()!!.tempDraft
                    currentLigaId = ligaId
                    onSuccess()
                } else {
                    onRequestFormation()
                }
            } catch (ex: Exception) {
                Log.e("DraftViewModel", "Error al recuperar draft: ${ex.message}")
                onRequestFormation()
            } finally {
                _isFetchingDraft.value = false
            }
        }
    }

    private fun parsePlayerOptionsJson(json: String): List<List<Any?>> =
        try {
            Gson().fromJson(json, object : TypeToken<List<List<Any?>>>() {}.type)
        } catch (e: Exception) {
            Log.e("DraftViewModel", "Error parseando playerOptions: ${e.message}")
            emptyList()
        }

    fun updateDraft(
        ligaId: Int,
        updatedPlayerOptions: List<List<Any?>>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val current = _tempDraft.value
            if (current == null) {
                _errorMessage.value = getString(R.string.error_draft_not_found)
                Log.e("DraftViewModel", "updateDraft: Draft no encontrado")
                return@launch
            }
            try {
                val req = UpdateDraftRequest(
                    plantillaId = current.idPlantilla,
                    playerOptions = updatedPlayerOptions
                )
                Log.d("DraftViewModel", "updateDraft req: $req")
                val resp = RetrofitClient.draftService.updateDraft(ligaId, req)
                Log.d("DraftViewModel", "updateDraft resp: code ${resp.code()} body ${resp.message()}")
                if (resp.isSuccessful) {
                    onSuccess()
                } else {
                    _errorMessage.value = getString(
                        R.string.error_update_draft,
                        resp.code(),
                        resp.message()
                    )
                    Log.e("DraftViewModel", "updateDraft error: ${resp.code()} - ${resp.message()}")
                }
            } catch (ex: Exception) {
                _errorMessage.value = getString(
                    R.string.error_connection,
                    ex.message.orEmpty()
                )
                Log.e("DraftViewModel", "updateDraft exception: ${ex.message}", ex)
            }
        }
    }

    fun saveDraft(
        selectedPlayers: Map<String, PlayerOption?>,
        onSuccess: () -> Unit
    ) {
        _isSavingDraft.value = true
        viewModelScope.launch {
            Log.d("DraftViewModel", "Entrando en saveDraft()")

            val current = _tempDraft.value
            if (current == null) {
                _errorMessage.value = getString(R.string.error_draft_not_found)
                Log.e("DraftViewModel", "Draft no encontrado")
                _isSavingDraft.value = false
                return@launch
            }

            val parsed = parsePlayerOptionsJson(current.playerOptions ?: "[]")
            val formationRows = when (_selectedFormation.value) {
                "4-3-3" -> listOf("Delantero" to 3, "Mediocentro" to 3, "Defensa" to 4, "Portero" to 1)
                "4-4-2" -> listOf("Delantero" to 2, "Mediocampista" to 4, "Defensa" to 4, "Portero" to 1)
                "3-4-3" -> listOf("Delantero" to 3, "Mediocampista" to 4, "Defensa" to 3, "Portero" to 1)
                else -> emptyList()
            }

            val finalOptions = mutableListOf<List<Any?>>()
            var idx = 0
            for ((posName, count) in formationRows) {
                repeat(count) {
                    val key = "${posName}_$it"
                    val selected = selectedPlayers[key]
                    if (selected == null) {
                        _errorMessage.value = getString(R.string.error_no_player_selected, key)
                        Log.e("DraftViewModel", "No se ha seleccionado un jugador para $key")
                        _isSavingDraft.value = false
                        return@launch
                    }

                    val group = parsed.getOrNull(idx)
                    if (group == null || group.size < 4) {
                        _errorMessage.value = getString(R.string.error_invalid_group, key)
                        Log.e("DraftViewModel", "Grupo inválido para $key")
                        _isSavingDraft.value = false
                        return@launch
                    }

                    val players = group.take(4).mapNotNull {
                        when (it) {
                            is Map<*, *> -> Gson().fromJson(Gson().toJson(it), PlayerOption::class.java)
                            is PlayerOption -> it
                            else -> null
                        }
                    }
                    if (players.size < 4) {
                        _errorMessage.value = getString(R.string.error_corrupt_group_data, key)
                        Log.e("DraftViewModel", "Datos corruptos en grupo $key")
                        _isSavingDraft.value = false
                        return@launch
                    }

                    val chosenIdx = players.indexOfFirst { it.id == selected.id }
                    if (chosenIdx == -1) {
                        _errorMessage.value = getString(R.string.error_player_option_mismatch, key)
                        Log.e("DraftViewModel", "Jugador seleccionado no coincide en $key")
                        _isSavingDraft.value = false
                        return@launch
                    }

                    finalOptions += group.take(4).toMutableList().apply { add(chosenIdx) }.toList()
                    idx++
                }
            }

            val tempFinal = TempDraftFinal(
                idPlantilla = current.idPlantilla,
                playerOptions = finalOptions.map { it.filterNotNull() }
            )
            val saveReq = SaveDraftRequest(tempDraft = tempFinal)
            Log.d("DraftViewModel", "Guardando draft: ${Gson().toJson(saveReq)}")

            try {
                val resp = RetrofitClient.draftService.saveDraft(saveReq)
                Log.d("DraftViewModel", "Código de respuesta: ${resp.code()}")
                if (resp.isSuccessful) {
                    onSuccess()
                } else {
                    val errText = resp.errorBody()?.string().orEmpty()
                    _errorMessage.value = getString(
                        R.string.error_save_draft,
                        resp.code(),
                        errText
                    )
                    Log.e("DraftViewModel", "Error al guardar: $errText")
                }
            } catch (ex: Exception) {
                _errorMessage.value = getString(R.string.error_connection, ex.message.orEmpty())
                Log.e("DraftViewModel", "Excepción: ${ex.message}", ex)
            } finally {
                _isSavingDraft.value = false
            }
        }
    }

    // Helper para obtener cadenas con parámetros
    private fun getString(id: Int, vararg args: Any): String =
        getApplication<Application>().getString(id, *args)
}
