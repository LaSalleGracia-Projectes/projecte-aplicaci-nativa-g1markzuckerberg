package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.PlayerModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.PlayerRepository
import kotlinx.coroutines.launch

data class PlayersUiState(
    val players: List<PlayerModel> = emptyList(),
    val selectedTeam: String? = null,
    val searchText: String = "",
    val pointsOrder: String = "down",
    val starFilter: Int = 0,            // 0=todos, 1–5 filtra exacto
    val isLoading: Boolean = true
) {
    val filtered: List<PlayerModel>
        get() {
            // 1) por equipo
            val byTeam = selectedTeam
                ?.let { t -> players.filter { it.teamName?.contains(t, true) == true } }
                ?: players
            // 2) por texto
            val byText = if (searchText.isBlank()) byTeam
            else byTeam.filter { it.displayName.contains(searchText, true) }
            // 3) por estrellas
            val byStars = if (starFilter == 0) byText
            else byText.filter { (it.estrellas ?: 0) == starFilter }
            // 4) ordenar puntos
            return if (pointsOrder == "up")
                byStars.sortedBy { it.puntosTotales ?: 0 }
            else
                byStars.sortedByDescending { it.puntosTotales ?: 0 }
        }
}

class PlayersViewModel(private val repo: PlayerRepository) : ViewModel() {
    var uiState by mutableStateOf(PlayersUiState())
        private set

    init { loadPlayers() }

    fun loadPlayers() = viewModelScope.launch {
        uiState = uiState.copy(isLoading = true)
        try {
            val loaded = repo.fetchPlayers().map {
                it.copy(
                    imagePath     = fullUrl(it.imagePath),
                    puntosTotales = it.puntosTotales ?: 0
                )
            }
            uiState = uiState.copy(players = loaded, isLoading = false)
            Log.d("PLAYERS_VM", "Cargados ${loaded.size} jugadores")
        } catch (e: Exception) {
            Log.e("PLAYERS_VM", "Error cargando", e)
            uiState = uiState.copy(isLoading = false)
        }
    }

    private fun fullUrl(path: String?) =
        if (path.isNullOrBlank()) "" else if (path.startsWith("http")) path else "https://cdn.sportmonks.com$path"

    fun onTeamSelected(team: String?) {
        uiState = uiState.copy(selectedTeam = team)
    }
    fun onSearch(q: String) {
        uiState = uiState.copy(searchText = q)
    }
    fun toggleOrder() {
        val next = if (uiState.pointsOrder == "up") "down" else "up"
        uiState = uiState.copy(pointsOrder = next)
    }
    fun toggleStarFilter() {
        // 0→1→2→3→4→5→0
        val next = (uiState.starFilter + 1) % 6
        uiState = uiState.copy(starFilter = next)
    }
}

class PlayersViewModelFactory(private val repo: PlayerRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        PlayersViewModel(repo) as T
}