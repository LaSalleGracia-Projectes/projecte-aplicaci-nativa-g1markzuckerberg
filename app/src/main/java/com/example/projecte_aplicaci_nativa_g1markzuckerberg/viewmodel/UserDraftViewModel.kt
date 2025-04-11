package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.LeagueUserResponse
import kotlinx.coroutines.launch

// Enum con las pestañas disponibles.
enum class Tab {
    USER, DRAFT
}

class UserDraftViewModel : ViewModel() {
    private val _selectedTab = MutableLiveData(Tab.USER)
    val selectedTab: LiveData<Tab> = _selectedTab

    // Nueva variable LiveData para almacenar la respuesta del endpoint
    private val _leagueUserResponse = MutableLiveData<LeagueUserResponse>()
    val leagueUserResponse: LiveData<LeagueUserResponse> = _leagueUserResponse

    fun setSelectedTab(tab: Tab) {
        _selectedTab.value = tab
    }

    // Función para llamar al endpoint y obtener la información del usuario en la liga
    fun fetchUserInfo(leagueId: String, userId: String) {
        viewModelScope.launch {
            try {
                // 🔍 Aquí imprimimos los valores que se están usando
                Log.d("UserDraft", "Calling URL: /liga/$leagueId/user/$userId")

                val response = RetrofitClient.ligaService.getUserFromLeague(leagueId, userId)
                if (response.isSuccessful) {
                    response.body()?.let { info ->
                        _leagueUserResponse.postValue(info)
                        Log.d("UserDraft", "User info: $info")
                    }
                } else {
                    Log.e("UserDraft", "Error fetching user info: ${response.code()} ${response.message()}")
                }
            } catch (e: Exception) {
                Log.e("UserDraft", "Exception during fetch: ${e.message}")
            }
        }
    }
}

