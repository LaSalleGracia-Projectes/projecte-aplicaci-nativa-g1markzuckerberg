package com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.TeamModel
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET

data class TeamsResponse(
    @SerializedName("teams") val teams: List<TeamModel>
)

interface TeamService {
    @GET("api/v1/liga/teams")
    suspend fun getAllTeams(): TeamsResponse
}