package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.LeagueUserResponse
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.Player
import kotlinx.coroutines.launch

// Enum con las pestañas disponibles.
enum class Tab {
    USER, DRAFT
}

class UserDraftViewModel(application: Application) : AndroidViewModel(application) {

    private val _selectedTab = MutableLiveData(Tab.USER)
    val selectedTab: LiveData<Tab> = _selectedTab

    private val _leagueUserResponse = MutableLiveData<LeagueUserResponse>()
    val leagueUserResponse: LiveData<LeagueUserResponse> = _leagueUserResponse

    private val _isLoadingDraft = MutableLiveData(false)
    val isLoadingDraft: LiveData<Boolean> = _isLoadingDraft

    private val _draftPlayers   = MutableLiveData<List<Player>>()
    private val _draftFormation = MutableLiveData<String>()

    val draftPlayers   : LiveData<List<Player>> = _draftPlayers
    val draftFormation : LiveData<String>       = _draftFormation

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

    /**
     * Llamada al endpoint para expulsar a un usuario.
     * onResult devuelve un mensaje traducido.
     */
    fun kickUser(
        leagueId: String,
        userId: String,
        onResult: (success: Boolean, message: String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.ligaService.kickUser(leagueId, userId)
                if (response.isSuccessful) {
                    onResult(true, getApplication<Application>().getString(R.string.user_kick_success))
                } else {
                    onResult(false, getApplication<Application>().getString(R.string.only_captain_error))
                }
            } catch (e: Exception) {
                val msg = getApplication<Application>().getString(
                    R.string.error_generic,
                    e.message ?: ""
                )
                onResult(false, msg)
            }
        }
    }

    /**
     * Función para asignar nuevo capitán.
     */
    fun makeCaptain(
        leagueId: String,
        newCaptainId: String,
        onResult: (success: Boolean, message: String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.ligaService.makeCaptain(leagueId, newCaptainId)
                if (response.isSuccessful) {
                    onResult(true, getApplication<Application>().getString(R.string.new_captain_assigned))
                } else {
                    onResult(false, getApplication<Application>().getString(R.string.only_captain_error))
                }
            } catch (e: Exception) {
                val msg = getApplication<Application>().getString(
                    R.string.error_generic,
                    e.message ?: ""
                )
                onResult(false, msg)
            }
        }
    }

    /**
     * Descarga la plantilla del usuario para la jornada dada (o total si roundName == null).
     */
    fun fetchUserDraft(leagueId: String, userId: String, roundName: Int?) {
        viewModelScope.launch {
            _isLoadingDraft.value = true
            try {
                val resp = RetrofitClient.draftService.getUserDraft(leagueId, userId, roundName)
                if (resp.isSuccessful) {
                    resp.body()?.let {
                        _draftPlayers.postValue(it.players)
                        _draftFormation.postValue(it.plantilla.formation)
                    }
                } else {
                    _draftPlayers.postValue(emptyList())
                    Log.e("UserDraft", "Error getUserDraft: ${resp.code()}")
                }
            } catch (e: Exception) {
                Log.e("UserDraft", "Excepción getUserDraft(): ${e.message}")
            } finally {
                _isLoadingDraft.value = false
            }
        }
    }
}
