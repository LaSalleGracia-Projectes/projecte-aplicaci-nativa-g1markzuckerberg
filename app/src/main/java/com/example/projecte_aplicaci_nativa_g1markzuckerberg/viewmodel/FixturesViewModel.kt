package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.Fixture
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.JornadaResponse
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.JornadaActualResponse
import kotlinx.coroutines.launch
import retrofit2.Response

class FixturesViewModel : ViewModel() {
    var fixturesState = mutableStateOf<List<Fixture>>(emptyList())
    var jornada = mutableStateOf("")
    var errorMessage = mutableStateOf("")


}
