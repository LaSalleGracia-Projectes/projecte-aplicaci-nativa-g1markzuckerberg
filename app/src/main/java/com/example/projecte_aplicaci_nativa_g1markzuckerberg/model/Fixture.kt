package com.example.projecte_aplicaci_nativa_g1markzuckerberg.model

data class Fixture(
    val id: Long,
    val name: String,
    val result_info: String?,
    val starting_at_timestamp: Long,
    val local_team_image: String?,
    val visitant_team_image: String?
)
