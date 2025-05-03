package com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface.PlayerService
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.PlayerIdModel
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.PlayerModel

class PlayerRepository(private val service: PlayerService) {

    suspend fun fetchPlayers(
        team: String? = null,
        pointsOrder: String? = null
    ): List<PlayerModel> =
        service.getAllPlayers(team, pointsOrder).body()?.players.orEmpty()

    suspend fun fetchPlayer(id: Int): PlayerIdModel? =
        service.getPlayerById(id).body()?.player
}