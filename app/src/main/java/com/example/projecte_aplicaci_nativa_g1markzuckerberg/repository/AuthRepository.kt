package com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface.AuthService
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.LoginRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.RegisterRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.TokenManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.util.Base64
import org.json.JSONObject

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

    suspend fun logoutMobile(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = service.logoutMobile()
                if (response.isSuccessful) {
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
                val response = service.loginWithGoogle(mapOf("idToken" to idToken))
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

    fun getCurrentUserId(): Int? {
        val token = tokenManager.getToken() ?: return null
        // El token JWT tiene tres partes separadas por puntos
        val parts = token.split(".")
        if (parts.size != 3) return null

        return try {
            // La segunda parte es el payload codificado en Base64Url
            val payloadEncoded = parts[1]
            // Decodificamos el payload. Los flags NO_WRAP y URL_SAFE ayudan a evitar espacios y saltos de línea.
            val payloadBytes = Base64.decode(payloadEncoded, Base64.URL_SAFE or Base64.NO_PADDING or Base64.NO_WRAP)
            val payload = String(payloadBytes, Charsets.UTF_8)
            // Convertimos el payload en un objeto JSON y extraemos el campo "id"
            val json = JSONObject(payload)
            json.optInt("id")
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

}
