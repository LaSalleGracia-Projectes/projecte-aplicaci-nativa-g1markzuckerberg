package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.Fixture

class FixturesViewModel : ViewModel() {
    var fixturesState = mutableStateOf<List<Fixture>>(emptyList())
    var errorMessage = mutableStateOf("")


}
