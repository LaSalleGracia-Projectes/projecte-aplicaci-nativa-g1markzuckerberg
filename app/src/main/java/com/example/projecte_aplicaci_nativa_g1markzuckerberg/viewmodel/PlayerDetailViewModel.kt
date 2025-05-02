package com.example.projecte_aplicaci_nativa_g1markzuckerberg.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.PlayerIdModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.PlayerRepository
import kotlinx.coroutines.launch

class PlayerDetailViewModel(
    private val playerId: Int,
    private val repo: PlayerRepository
) : ViewModel() {

    var player    by mutableStateOf<PlayerIdModel?>(null)
        private set

    var isLoading by mutableStateOf(true)
        private set

    var errorMessage by mutableStateOf<String?>(null)
        private set

    init {
        loadPlayer()
    }

    private fun loadPlayer() = viewModelScope.launch {
        isLoading = true
        errorMessage = null
        try {
            val p = repo.fetchPlayer(playerId)
            if (p == null) {
                errorMessage = "Jugador no encontrado"
                Log.w("DETAIL_VM", "Jugador $playerId no existe")
            } else {
                // asegúrate de que la imagen es URL completa
                val fixedImage = if (p.imagePath.startsWith("http")) p.imagePath
                else "https://cdn.sportmonks.com${p.imagePath}"
                player = p.copy(imagePath = fixedImage)
                Log.d("DETAIL_VM", "Jugador cargado: $player")
            }
        } catch (e: Exception) {
            Log.e("DETAIL_VM", "Error al cargar jugador $playerId", e)
            errorMessage = "Error cargando jugador"
        } finally {
            isLoading = false
        }
    }
}

class PlayerDetailViewModelFactory(
    private val playerId: Int,
    private val repo: PlayerRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T =
        PlayerDetailViewModel(playerId, repo) as T
}
