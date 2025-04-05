package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.CreateLigaRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.CreateLigaResponse
import kotlinx.coroutines.launch

class CreateLigaViewModel : ViewModel() {

    private val _createLigaResult = MutableLiveData<CreateLigaResponse?>()
    val createLigaResult: LiveData<CreateLigaResponse?> = _createLigaResult

    private val _errorMessage = MutableLiveData<String>("")
    val errorMessage: LiveData<String> = _errorMessage

    fun createLiga(name: String) {
        viewModelScope.launch {
            try {
                val request = CreateLigaRequest(name = name)
                // Usamos el servicio correcto para crear la liga
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
}
