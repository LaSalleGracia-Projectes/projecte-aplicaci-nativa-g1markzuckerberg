package com.example.projecte_aplicaci_nativa_g1markzuckerberg.model

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface.TeamService

data class TeamModel(
    val id: Int,
    val name: String,
    val imagePath: String
)

class TeamRepository(private val service: TeamService) {
    suspend fun getAllTeams(): List<TeamModel> = service.getAllTeams().teams
}