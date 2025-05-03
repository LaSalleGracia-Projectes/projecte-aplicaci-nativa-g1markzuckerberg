package com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.PlayerDetailResponse
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.PlayerResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PlayerService {

    @GET("api/v1/player")
    suspend fun getAllPlayers(
        @Query("team")   team   : String?,
        @Query("points") points : String? = null      // "up" | "down"
    ): Response<PlayerResponse>

    /**  GET /api/v1/player/{id}          */
    @GET("api/v1/player/{id}")
    suspend fun getPlayerById(
        @Path("id") id: Int
    ): Response<PlayerDetailResponse>
}