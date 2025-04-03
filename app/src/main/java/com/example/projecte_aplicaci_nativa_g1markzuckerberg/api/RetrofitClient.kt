package com.example.projecte_aplicaci_nativa_g1markzuckerberg.api

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface.AuthService
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.interfaz.SportMonksService
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.network.AuthInterceptor
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.AuthRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Si usas el emulador de Android, localhost es 10.0.2.2
    private const val BASE_URL = "http://10.0.2.2:3000/"

    // Este repositorio se asignará desde tu Application o mediante DI
    lateinit var authRepository: AuthRepository

    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor { authRepository.getToken() })
            .build()
    }

    val service: SportMonksService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SportMonksService::class.java)
    }
    // Retrofit para autenticación
    val authService: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient) // Se utiliza el mismo interceptor para agregar el token si lo hay
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }
}
