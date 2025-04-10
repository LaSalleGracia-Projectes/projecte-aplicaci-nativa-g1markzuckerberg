package com.example.projecte_aplicaci_nativa_g1markzuckerberg.model

data class UserInfo(
    val id: Int,
    val username: String,
    val correo: String,
    val is_admin: Boolean,
    val created_at: String,
    val birthDate: String?
)