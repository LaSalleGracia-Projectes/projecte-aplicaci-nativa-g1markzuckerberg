package com.example.projecte_aplicaci_nativa_g1markzuckerberg.model

data class UpdateUsernameRequest(val username: String)

data class UpdateBirthDateRequest(val birthDate: String)   // formato "YYYY-MM-DD"

data class UpdatePasswordRequest(
    val password: String,
    val newPassword: String,
    val confirmPassword: String
)

data class SimpleApiResponse(
    val message: String,
    val user: UserInfo? = null      // el backend puede incluir el usuario actualizado
)