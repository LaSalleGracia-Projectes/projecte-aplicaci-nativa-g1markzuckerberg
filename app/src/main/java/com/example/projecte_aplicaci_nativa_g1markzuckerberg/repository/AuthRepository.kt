package com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface.AuthService
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.LoginRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.RegisterRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(
    private val service: AuthService,
    private val tokenManager: TokenManager
) {

    suspend fun login(correo: String, password: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val request = LoginRequest(correo, password)
                val response = service.login(request)
                if (response.isSuccessful) {
                    response.body()?.tokens?.mobileToken?.let { token ->
                        tokenManager.saveToken(token)
                        Result.success(token)
                    } ?: Result.failure(Exception("Respuesta vacía"))
                } else {
                    Result.failure(Exception("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun register(username: String, correo: String, password: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val request = RegisterRequest(username, correo, password)
                val response = service.register(request)
                if (response.isSuccessful) {
                    response.body()?.tokens?.mobileToken?.let { token ->
                        tokenManager.saveToken(token)
                        Result.success(token)
                    } ?: Result.failure(Exception("Respuesta vacía"))
                } else {
                    Result.failure(Exception("Error: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    fun getToken(): String? = tokenManager.getToken()

    // En AuthRepository.kt
    suspend fun logoutMobile(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                // Ajusta si tu AuthService tiene el método logoutMobile
                val response = service.logoutMobile()
                if (response.isSuccessful) {
                    // Limpia el token local
                    tokenManager.clearToken()
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Error al cerrar sesión: ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
    suspend fun loginWithGoogle(idToken: String): Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val response = service.loginWithGoogle(idToken)
                if (response.isSuccessful) {
                    response.body()?.mobileToken?.let { token ->
                        tokenManager.saveToken(token)
                        Result.success(token)
                    } ?: Result.failure(Exception("Token no recibido"))
                } else {
                    Result.failure(Exception("Error ${response.code()}"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }


}
