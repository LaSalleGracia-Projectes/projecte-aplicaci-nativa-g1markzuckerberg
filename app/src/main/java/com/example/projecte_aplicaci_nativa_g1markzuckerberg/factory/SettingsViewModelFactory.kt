package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.factory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.AuthRepository
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.ContactRepository
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel.SettingsViewModel

class SettingsViewModelFactory(
    private val authRepository: AuthRepository,
    private val contactRepository: ContactRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        when {
            modelClass.isAssignableFrom(SettingsViewModel::class.java) ->
                SettingsViewModel(authRepository, contactRepository) as T
            else -> throw IllegalArgumentException("Unknown ViewModel class")
        }
}
