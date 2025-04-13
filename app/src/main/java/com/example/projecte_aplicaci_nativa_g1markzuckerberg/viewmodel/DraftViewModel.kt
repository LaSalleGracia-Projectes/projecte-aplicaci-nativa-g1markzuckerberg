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

    fun createDraft(formation: String, ligaId: Int) {
        viewModelScope.launch {
            try {
                // Construir el objeto request usando solo formation y liga.
                val request = CreateDraftRequest(
                    formation = formation,
                    ligaId = ligaId
                    )
                Log.d("DraftViewModel", "Enviando request de crear draft: $request")

                val response = RetrofitClient.draftService.createDraft(request)
                Log.d("DraftViewModel", "Respuesta de crear draft: código ${response.code()} - body: ${response.body()}")

                if (response.isSuccessful && response.body() != null) {
                    _tempDraft.value = response.body()  // Se guarda el draft temporal devuelto
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




    // Función para actualizar el draft (por ejemplo, cuando se modifica la selección)
    fun updateDraft(plantillaId: Int, playerOptions: List<PositionOptions>) {
        viewModelScope.launch {
            try {
                val request = UpdateDraftRequest(plantillaId, playerOptions)
                val response = RetrofitClient.draftService.updateDraft(request)
                if (!response.isSuccessful) {
                    _errorMessage.value = "Error al actualizar el draft: ${response.code()} ${response.message()}"
                }
            } catch (ex: Exception) {
                _errorMessage.value = "Error de conexión: ${ex.message}"
            }
        }
    }

    // Función para guardar el draft final
    fun saveDraft() {
        viewModelScope.launch {
            val currentDraft = _tempDraft.value
            if (currentDraft == null) {
                _errorMessage.value = "No hay draft para guardar"
                return@launch
            }
            try {
                val request = SaveDraftRequest(currentDraft)
                val response = RetrofitClient.draftService.saveDraft(request)
                if (!response.isSuccessful) {
                    _errorMessage.value = "Error al guardar el draft: ${response.code()} ${response.message()}"
                } else {
                    _errorMessage.value = "Draft guardado exitosamente"
                }
            } catch (ex: Exception) {
                _errorMessage.value = "Error de conexión: ${ex.message}"
            }
        }
    }

    // Función para actualizar la selección de una posición en el draft
    // La implementación dependerá de cómo estructures PositionOptions; a modo de ejemplo:
    fun onOptionSelectedForPosition(
        options: List<Any>,
        selectedIndex: Int
    ) {
        // Aquí podrías actualizar localmente la _tempDraft con la selección nueva.
        // Es decir, recorrer tempDraft.playerOptions, buscar el grupo que sea igual a options,
        // y actualizar el quinto elemento con el índice seleccionado.
        // Luego, emitir un update al LiveData _tempDraft y opcionalmente llamar a updateDraft().
    }
    fun getPlayerOptionsByPosition(): List<List<Player>> {
        return _tempDraft.value?.playerOptions
            ?.mapNotNull { positionGroup ->
                try {
                    @Suppress("UNCHECKED_CAST")
                    (positionGroup as List<Map<String, Any>?>)
                        .filterNotNull()
                        .map {
                            Player(
                                id = it["id"].toString(),
                                displayName = it["displayName"].toString(),
                                positionId = (it["positionId"] as String).toInt(),
                                imagePath = it["imagePath"].toString(),
                                estrellas = (it["estrellas"] as Double).toInt(),
                                puntos_totales = it["puntos_totales"].toString()
                            )
                        }
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }
            } ?: emptyList()
    }

}
