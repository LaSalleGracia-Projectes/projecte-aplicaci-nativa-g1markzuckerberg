package com.example.projecte_aplicaci_nativa_g1markzuckerberg.model

data class PlayerModel(
    val id: Int,
    val displayName: String,
    val positionId: Int,
    val imagePath: String,
    val estrellas: Int? = null,
    val puntosTotales: Int? = null,
    val teamId: Int,
    val teamName: String? = null, // Nombre del equipo
    val teamImage: String? = null
)

data class PlayerResponse(
    val players: List<PlayerModel>
)