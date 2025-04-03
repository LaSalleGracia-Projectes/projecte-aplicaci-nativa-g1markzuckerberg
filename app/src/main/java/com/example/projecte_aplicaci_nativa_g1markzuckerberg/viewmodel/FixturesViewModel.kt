package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.Fixture
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.JornadaResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class FixturesViewModel : ViewModel() {
    // Estados para guardar la lista de fixtures y la jornada
    var fixturesState = mutableStateOf<List<Fixture>>(emptyList())
    var jornada = mutableStateOf("")
    var errorMessage = mutableStateOf("")

    init {
        fetchFixtures()
    }

    private fun fetchFixtures() {
        viewModelScope.launch {
            try {
                // Llamamos al método que devuelve la jornada 28, por ejemplo
                val response: Response<JornadaResponse> = RetrofitClient.service.getJornadaFixtures()
                if (response.isSuccessful) {
                    response.body()?.let { data ->
                        // data.jornada y data.fixtures vienen de JornadaResponse
                        jornada.value = data.jornada
                        fixturesState.value = data.fixtures
                    }
                } else {
                    errorMessage.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                errorMessage.value = e.message ?: "Error desconocido"
            }
        }
    }
}
