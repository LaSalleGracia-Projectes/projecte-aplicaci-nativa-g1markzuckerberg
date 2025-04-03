package com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.LoginRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.LoginResponse
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.RegisterRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.RegisterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("api/v1/auth/loginMobile")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @POST("api/v1/auth/signupMobile")
    suspend fun register(@Body request: RegisterRequest): Response<RegisterResponse>

    @POST("api/v1/auth/logoutMobile")
    suspend fun logoutMobile(): Response<Unit>

}