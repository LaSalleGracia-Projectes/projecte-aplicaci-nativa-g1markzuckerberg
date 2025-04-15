package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.*
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.*
import kotlinx.coroutines.launch

class DraftViewModel : ViewModel() {

    // Estado para la plantilla temporal (draft)
    private val _tempDraft = MutableLiveData<TempPlantillaResponse?>()
    val tempDraft: LiveData<TempPlantillaResponse?> = _tempDraft

    // Estado para mensajes de error
    private val _errorMessage = MutableLiveData("")
    val errorMessage: LiveData<String> = _errorMessage

    private val _selectedFormation = mutableStateOf("4-3-3")
    val selectedFormation: State<String> = _selectedFormation

    fun setSelectedFormation(formation: String) {
        _selectedFormation.value = formation
    }

    fun createDraft(
        formation: String,
        ligaId: Int,
        onSuccess: () -> Unit
    ) {        viewModelScope.launch {
            try {
                // Construir el objeto request usando solo formation y liga.
                val request = CreateDraftRequest(
                    formation = formation,
                    ligaId = ligaId
                    )
                Log.d("DraftViewModel", "Enviando request de crear draft: $request")

                val response = RetrofitClient.draftService.createDraft(request)
                Log.d("DraftViewModel", "Respuesta de crear draft: código ${response.code()} - body: ${response.body()}")

                if (response.isSuccessful) {
                    _tempDraft.value = response.body()!!.tempDraft
                    Log.d("DraftViewModel", "TempDraft recibido y guardado en LiveData: ${_tempDraft.value}")
                    Log.d("DraftViewModel", "Se recibieron ${_tempDraft.value?.playerOptions?.size ?: 0} grupos de jugadores")

                    onSuccess()
                } else {
                    _errorMessage.value = "Error al crear el draft: ${response.code()} ${response.message()}"
                    Log.e("DraftViewModel", "Error al crear el draft: ${response.code()} ${response.message()}")
                }
            } catch (ex: Exception) {
                _errorMessage.value = "Error de conexión: ${ex.message}"
                Log.e("DraftViewModel", "Exception al crear el draft: ${ex.message}")
            }
        }
    }

    fun saveDraft(
        selectedPlayers: Map<String, PlayerOption?>,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val currentDraft = _tempDraft.value ?: run {
                _errorMessage.value = "Draft no encontrado."
                return@launch
            }

            // Define la estructura de la formación igual que en el layout.
            val formationRows: List<Pair<String, Int>> = when (_selectedFormation.value) {
                "4-3-3" -> listOf("Delantero" to 3, "Mediocentro" to 3, "Defensa" to 4, "Portero" to 1)
                "4-4-2" -> listOf("Delantero" to 2, "Mediocampista" to 4, "Defensa" to 4, "Portero" to 1)
                "3-4-3" -> listOf("Delantero" to 3, "Mediocampista" to 4, "Defensa" to 3, "Portero" to 1)
                else -> emptyList()
            }

            // Construir la nueva lista de grupos.
            val finalPlayerOptions = mutableListOf<List<Any>>()
            var groupIndex = 0
            for ((positionName, count) in formationRows) {
                repeat(count) { index ->
                    val key = "${positionName}_$index"
                    val selectedPlayer = selectedPlayers[key]
                    if (selectedPlayer == null) {
                        _errorMessage.value = "No se ha seleccionado un jugador para $key."
                        return@launch
                    }
                    // Obtén el grupo original desde el draft y toma las 4 opciones iniciales.
                    val group = currentDraft.playerOptions?.getOrNull(groupIndex)
                    if (group == null || group.size < 4) {
                        _errorMessage.value = "Grupo de jugadores inválido para $key."
                        return@launch
                    }
                    // Encuentra el índice del jugador seleccionado (buscando en los primeros 4 elementos).
                    val chosenIndex = group.take(4).indexOfFirst { it.id == selectedPlayer.id }
                    if (chosenIndex == -1) {
                        _errorMessage.value = "El jugador seleccionado en $key no coincide con las opciones."
                        return@launch
                    }
                    // Construye el nuevo grupo: los 4 jugadores + índice seleccionado.
                    val newGroup = group.take(4).toMutableList<Any>().apply { add(chosenIndex) }
                    finalPlayerOptions.add(newGroup)
                    groupIndex++
                }
            }

            // Se crea el objeto final que se enviará en el request.
            val tempDraftFinal = TempDraftFinal(
                idPlantilla = currentDraft.idPlantilla,
                playerOptions = finalPlayerOptions
            )
            val saveRequest = SaveDraftRequest(tempDraft = tempDraftFinal)

            try {
                // Se asume que RetrofitClient.draftService.saveDraft(saveRequest) ya está definido.
                val response = RetrofitClient.draftService.saveDraft(saveRequest)
                if (response.isSuccessful) {
                    onSuccess()
                } else {
                    _errorMessage.value = "Error al guardar el draft: ${response.code()} ${response.message()}"
                }
            } catch (ex: Exception) {
                _errorMessage.value = "Error de conexión: ${ex.message}"
            }
        }
    }





}
