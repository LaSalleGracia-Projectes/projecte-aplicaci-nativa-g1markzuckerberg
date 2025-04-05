package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient.authRepository
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.Fixture
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.JornadaResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class HomeLogedViewModel : ViewModel() {

    private val _jornadaData = mutableStateOf<JornadaResponse?>(null)
    val jornadaData: State<JornadaResponse?> = _jornadaData

    private val _fixturesState = mutableStateOf<List<Fixture>>(emptyList())
    val fixturesState: State<List<Fixture>> = _fixturesState

    init {
        // Cargar por defecto la jornada 28
        fetchCurrentJornada()
    }

    private fun fetchCurrentJornada() {
        viewModelScope.launch {
            try {
                // Primero obtenemos la jornada actual
                val currentResponse = RetrofitClient.service.getJornadaActual()
                if (currentResponse.isSuccessful) {
                    val currentJornada = currentResponse.body()?.jornadaActual
                    if (currentJornada != null) {
                        Log.d("API_CALL", "Jornada actual recibida: ${currentJornada.name}")
                        // Usamos el valor recibido (ej. "30") para obtener los fixtures
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
                        // Ordenamos los fixtures por starting_at_timestamp de forma ascendente
                        val sortedFixtures = data.fixtures.sortedBy { it.starting_at_timestamp }
                        _fixturesState.value = sortedFixtures
                        // Actualizamos _jornadaData, asegurándonos de incluir los fixtures ordenados
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


}
