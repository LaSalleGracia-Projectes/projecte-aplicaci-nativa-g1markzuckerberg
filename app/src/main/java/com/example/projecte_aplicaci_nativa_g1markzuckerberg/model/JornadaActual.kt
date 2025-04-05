package com.example.projecte_aplicaci_nativa_g1markzuckerberg.model

data class JornadaActual(
    val name: String,
    val id: Int,
    val season_id: Int,
    val is_current: Boolean,
    val starting_at: String,
    val ending_at: String
)