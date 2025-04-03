package com.example.proyecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    fun onCrearCuenta() {
        viewModelScope.launch {
            // TODO: Lógica para crear cuenta
        }
    }

    fun onIniciarSesion() {
        viewModelScope.launch {
            // TODO: Lógica para iniciar sesión
        }
    }
}
