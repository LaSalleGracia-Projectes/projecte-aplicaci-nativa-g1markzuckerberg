package com.example.projecte_aplicaci_nativa_g1markzuckerberg.model

data class LeagueUser(
    val id: Int,
    val username: String,
    val birthDate: String?,
    val puntos_totales: String,
    val is_capitan: Boolean,
    val imageUrl: String
)