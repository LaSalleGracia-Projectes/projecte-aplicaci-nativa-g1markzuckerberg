package com.example.projecte_aplicaci_nativa_g1markzuckerberg.model

data class LigaUsersResponse(
    val liga: LigaDetails,
    val users: List<LigaUser>,
    val jornada_id: Int
)