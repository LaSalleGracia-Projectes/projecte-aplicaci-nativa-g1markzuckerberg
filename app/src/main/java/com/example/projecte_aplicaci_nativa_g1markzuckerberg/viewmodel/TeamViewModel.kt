package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.TeamModel
import kotlinx.coroutines.launch
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModelProvider
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.TeamRepository

class TeamViewModel(private val repository: TeamRepository) : ViewModel() {
    val teams = mutableStateListOf<TeamModel>()
    var isLoading by mutableStateOf(true)
    var error by mutableStateOf<String?>(null)

    init {
        viewModelScope.launch {
            try {
                val data = repository.getAllTeams()
                teams.addAll(data)
            } catch (e: Exception) {
                error = e.message
            } finally {
                isLoading = false
            }
        }
    }
}

class TeamViewModelFactory(private val repository: TeamRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TeamViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TeamViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
