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




}
