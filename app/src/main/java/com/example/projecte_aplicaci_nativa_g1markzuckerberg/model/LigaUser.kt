package com.example.projecte_aplicaci_nativa_g1markzuckerberg.model

import com.google.gson.annotations.SerializedName

data class LigaUser(
    @SerializedName("id")
    val usuario_id: Int,
    val username: String,
    val puntos_jornada: Int,
    val puntos_acumulados: Int,
    val imageUrl: String
)
