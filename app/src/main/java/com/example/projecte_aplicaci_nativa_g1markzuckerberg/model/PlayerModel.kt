package com.example.projecte_aplicaci_nativa_g1markzuckerberg.model

import com.google.gson.annotations.SerializedName

data class PlayerModel(
    val id: Int,
    val displayName: String,
    val positionId: Int,

    // Mapeo compatible con "image_path" y "playerImage"
    @SerializedName(value = "image_path", alternate = ["playerImage"])
    val imagePath: String,

    // Mapeo compatible con "puntos_totales" y "points"
    @SerializedName(value = "puntos_totales", alternate = ["points"])
    val puntosTotales: Int? = null,

    val estrellas: Int? = null,

    val teamId: Int,
    val teamName: String? = null,
    val teamImage: String? = null
)

data class PlayerIdModel(
    val id: Int,
    val displayName: String,
    val positionId: Int,

    @SerializedName("playerImage")
    val imagePath: String,

    @SerializedName("points")
    val puntosTotales: Int? = null,

    val estrellas: Int? = null,

    val teamId: Int,
    val teamName: String? = null,
    val teamImage: String? = null
)

data class PlayerResponse(
    val players: List<PlayerModel>
)

data class PlayerDetailResponse( val player : PlayerIdModel )