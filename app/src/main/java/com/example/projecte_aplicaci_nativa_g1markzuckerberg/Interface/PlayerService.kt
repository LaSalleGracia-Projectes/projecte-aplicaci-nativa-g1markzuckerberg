package com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.PlayerResponse
import retrofit2.Response
import retrofit2.http.GET

interface PlayerService {
    @GET ("api/v1/player")
    suspend fun getAllPlayers(): Response<PlayerResponse>
}