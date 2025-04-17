package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.launch

class DraftViewModel : ViewModel() {

    private val _tempDraft = MutableLiveData<TempPlantillaResponse?>()
    val tempDraft: LiveData<TempPlantillaResponse?> = _tempDraft

    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    private val _selectedFormation = mutableStateOf("4-3-3")
    val selectedFormation: State<String> = _selectedFormation

    var currentLigaId: Int = 0
        private set

    fun setSelectedFormation(formation: String) {
        _selectedFormation.value = formation
    }
    fun createAndFetchDraft(
        formation: String,
        ligaId: Int,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            // 1) Intentamos el POST, pero capturamos y sólo logueamos errores de parseo:
            try {
                val createResp = RetrofitClient.draftService.createDraft(
                    CreateDraftRequest(formation, ligaId)
                )
                Log.d("DraftViewModel",
                    "createDraft() code=${createResp.code()} — ignoramos body")
            } catch (ex: Exception) {
                Log.w("DraftViewModel",
                    "Error parseando createDraft, lo ignoramos y seguimos al GET", ex)
            }

            // 2) Ahora, hagamos siempre el GET /draft
            try {
                val fetchResp = RetrofitClient.draftService.getTempDraft(ligaId)
                if (fetchResp.isSuccessful) {
                    _tempDraft.value = fetchResp.body()!!.tempDraft
                    currentLigaId = ligaId
                    Log.d("DraftViewModel", "GET tempDraft OK — navegamos")
                    onSuccess()
                } else {
                    _errorMessage.value = "Error al recuperar draft: ${fetchResp.code()}"
                    Log.e("DraftViewModel",
                        "GET tempDraft error ${fetchResp.code()}")
                }
            } catch (ex: Exception) {
                _errorMessage.value = "Error de conexión al recuperar draft: ${ex.message}"
                Log.e("DraftViewModel", "Excepción en GET tempDraft", ex)
            }
        }
    }



    fun createOrFetchDraft(
        ligaId: Int,
        onSuccess: () -> Unit,
        onRequestFormation: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.draftService.getTempDraft(ligaId)
                Log.d("DraftViewModel", "Respuesta de obtener draft: código ${response.code()} - body: ${response.body()}")
                if (response.isSuccessful) {
                    _tempDraft.value = response.body()!!.tempDraft
                    currentLigaId = ligaId
                    onSuccess()
                } else {
                    Log.d("DraftViewModel", "No se encontró draft existente. Se pedirá formación")
                    onRequestFormation()
                }
            } catch (ex: Exception) {
                Log.e("DraftViewModel", "Error al recuperar draft: ${ex.message}")
                onRequestFormation()
            }
        }
    }

    private fun parsePlayerOptionsJson(json: String): List<List<Any?>> {
        return try {
            Gson().fromJson(json, object : TypeToken<List<List<Any?>>>() {}.type)
        } catch (e: Exception) {
            Log.e("DraftViewModel", "Error al parsear playerOptions: ${e.message}")
            emptyList()
        }
    }

    fun updateDraft(
        ligaId: Int,
        updatedPlayerOptions: List<List<Any?>>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val currentDraft = _tempDraft.value ?: run {
                _errorMessage.value = "Draft no encontrado."
                Log.e("DraftViewModel", "updateDraft: Draft no encontrado")
                return@launch
            }
            try {
                val updateRequest = UpdateDraftRequest(
                    plantillaId = currentDraft.idPlantilla,
                    playerOptions = updatedPlayerOptions
                )
                Log.d("DraftViewModel", "updateDraft request: $updateRequest")
                val response = RetrofitClient.draftService.updateDraft(ligaId, updateRequest)
                Log.d("DraftViewModel", "updateDraft response code: ${response.code()} - body: ${response.body()}")
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    _errorMessage.value = "Error al actualizar el draft: ${response.code()} ${response.message()}"
                    Log.e("DraftViewModel", "updateDraft error: ${response.code()} - ${response.message()}")
                }
            } catch (ex: Exception) {
                _errorMessage.value = "Error de conexión: ${ex.message}"
                Log.e("DraftViewModel", "updateDraft exception: ${ex.message}", ex)
            }
        }
    }

    fun saveDraft(
        selectedPlayers: Map<String, PlayerOption?>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            Log.d("DraftViewModel", "🧠 Entrando en saveDraft()")

            val currentDraft = _tempDraft.value ?: run {
                _errorMessage.value = "Draft no encontrado."
                Log.e("DraftViewModel", "❌ Draft no encontrado")
                return@launch
            }

            val parsedGroups = parsePlayerOptionsJson(currentDraft.playerOptions ?: "[]")
            val formationRows = when (_selectedFormation.value) {
                "4-3-3" -> listOf("Delantero" to 3, "Mediocentro" to 3, "Defensa" to 4, "Portero" to 1)
                "4-4-2" -> listOf("Delantero" to 2, "Mediocampista" to 4, "Defensa" to 4, "Portero" to 1)
                "3-4-3" -> listOf("Delantero" to 3, "Mediocampista" to 4, "Defensa" to 3, "Portero" to 1)
                else -> emptyList()
            }

            val finalPlayerOptions = mutableListOf<List<Any?>>()
            var groupIndex = 0

            for ((positionName, count) in formationRows) {
                repeat(count) {
                    val key = "${positionName}_$it"
                    val selectedPlayer = selectedPlayers[key]
                    if (selectedPlayer == null) {
                        _errorMessage.value = "No se ha seleccionado un jugador para $key."
                        Log.e("DraftViewModel", "❌ No se ha seleccionado un jugador para $key.")
                        return@launch
                    }

                    val group = parsedGroups.getOrNull(groupIndex)
                    if (group == null || group.size < 4) {
                        _errorMessage.value = "Grupo de jugadores inválido para $key."
                        Log.e("DraftViewModel", "❌ Grupo de jugadores inválido para $key.")
                        return@launch
                    }

                    val players = group.take(4).mapNotNull { item ->
                        when (item) {
                            is Map<*, *> -> Gson().fromJson(Gson().toJson(item), PlayerOption::class.java)
                            is PlayerOption -> item
                            else -> null
                        }
                    }
                    if (players.size < 4) {
                        _errorMessage.value = "Los datos del grupo $key están corruptos."
                        Log.e("DraftViewModel", "❌ Los datos del grupo $key están corruptos.")
                        return@launch
                    }

                    val chosenIndex = players.indexOfFirst { it.id == selectedPlayer.id }
                    if (chosenIndex == -1) {
                        _errorMessage.value = "El jugador seleccionado en $key no coincide con las opciones."
                        Log.e("DraftViewModel", "❌ El jugador seleccionado en $key no coincide.")
                        return@launch
                    }

                    val newGroup = group.take(4).toMutableList().apply { add(chosenIndex) }
                    finalPlayerOptions.add(newGroup.toList())
                    groupIndex++
                }
            }

            val tempDraftFinal = TempDraftFinal(
                idPlantilla = currentDraft.idPlantilla,
                playerOptions = finalPlayerOptions.map { it.filterNotNull() } // quitar nulls por si acaso
            )

            val saveRequest = SaveDraftRequest(tempDraft = tempDraftFinal)

            Log.d("DraftViewModel", "✅ Guardando draft...")
            Log.d("DraftViewModel", "TempDraftFinal.idPlantilla = ${tempDraftFinal.idPlantilla}")
            tempDraftFinal.playerOptions.forEachIndexed { index, grupo ->
                Log.d("DraftViewModel", "Grupo $index:")
                grupo.forEachIndexed { i, jugador ->
                    Log.d("DraftViewModel", "   [$i] = ${Gson().toJson(jugador)}")
                }
            }
            val jsonRequest = Gson().toJson(saveRequest)
            Log.d("DraftViewModel", "📦 JSON enviado: $jsonRequest")

            try {
                val response = RetrofitClient.draftService.saveDraft(saveRequest)
                Log.d("DraftViewModel", "📡 Código de respuesta: ${response.code()}")
                if (response.isSuccessful) {
                    Log.d("DraftViewModel", "✅ Draft guardado correctamente.")
                    onSuccess()
                } else {
                    val errorText = response.errorBody()?.string()
                    _errorMessage.value = "Error al guardar: ${response.code()} $errorText"
                    Log.e("DraftViewModel", "❌ Error: $errorText")
                }
            } catch (ex: Exception) {
                _errorMessage.value = "Error de conexión: ${ex.message}"
                Log.e("DraftViewModel", "❌ Excepción: ${ex.message}", ex)
            }
        }
    }

}
