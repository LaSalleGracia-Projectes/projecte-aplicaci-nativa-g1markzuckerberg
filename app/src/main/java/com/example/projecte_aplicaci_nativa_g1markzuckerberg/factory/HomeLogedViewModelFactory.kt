package com.example.projecte_aplicaci_nativa_g1markzuckerberg.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.AuthRepository
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.HomeLogedViewModel

class HomeLogedViewModelFactory(private val authRepository: AuthRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeLogedViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeLogedViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
