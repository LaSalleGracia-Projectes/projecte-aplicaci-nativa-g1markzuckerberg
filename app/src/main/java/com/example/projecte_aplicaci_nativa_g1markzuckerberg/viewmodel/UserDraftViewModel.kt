package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.LeagueUserResponse
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.Player
import kotlinx.coroutines.launch

// Enum con las pestañas disponibles.
enum class Tab {
    USER, DRAFT
}

class UserDraftViewModel : ViewModel() {
    private val _selectedTab = MutableLiveData(Tab.USER)
    val selectedTab: LiveData<Tab> = _selectedTab

    private val _leagueUserResponse = MutableLiveData<LeagueUserResponse>()
    val leagueUserResponse: LiveData<LeagueUserResponse> = _leagueUserResponse

    fun setSelectedTab(tab: Tab) {
        _selectedTab.value = tab
    }



    fun fetchUserInfo(leagueId: String, userId: String) {
        viewModelScope.launch {
            try {
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

    // Llamada al endpoint para expulsar a un usuario
    fun kickUser(leagueId: String, userId: String, onResult: (success: Boolean, message: String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.ligaService.kickUser(leagueId, userId)
                if (response.isSuccessful) {
                    onResult(true, "Usuario expulsado correctamente")
                } else {
                    onResult(false, "Solo el capitán de la liga puede hacer esta acción")
                }
            } catch (e: Exception) {
                onResult(false, "Error: ${e.message}")
            }
        }
    }

    // Función para asignar nuevo capitán, con callback de resultado
    fun makeCaptain(leagueId: String, newCaptainId: String, onResult: (success: Boolean, message: String) -> Unit) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.ligaService.makeCaptain(leagueId, newCaptainId)
                if (response.isSuccessful) {
                    onResult(true, "Nuevo capitán asignado correctamente")
                } else {
                    onResult(false, "Solo el capitán de la liga puede hacer esta acción")
                }
            } catch (e: Exception) {
                onResult(false, "Error: ${e.message}")
            }
        }
    }

    private val _draftPlayers   = MutableLiveData<List<Player>>()     // lista plana de 11
    private val _draftFormation = MutableLiveData<String>()           // "4-3-3", "3-4-3", …
    val draftPlayers   : LiveData<List<Player>> = _draftPlayers
    val draftFormation : LiveData<String>       = _draftFormation

    /** Descarga la plantilla del usuario para la jornada dada (o total si roundName == null) */
    fun fetchUserDraft(leagueId: String, userId: String, roundName: Int?) {
        viewModelScope.launch {
            try {
                val resp = RetrofitClient.draftService.getUserDraft(leagueId, userId, roundName)
                if (resp.isSuccessful) {
                    resp.body()?.let {
                        _draftPlayers.postValue(it.players)
                        _draftFormation.postValue(it.plantilla.formation)
                    }
                } else {
                    Log.e("UserDraft", "Error getUserDraft: ${resp.code()}")
                }
            } catch (e: Exception) {
                Log.e("UserDraft", "Excepción getUserDraft(): ${e.message}")
            }
        }
    }
}