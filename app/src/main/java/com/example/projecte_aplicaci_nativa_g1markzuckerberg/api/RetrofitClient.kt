package com.example.projecte_aplicaci_nativa_g1markzuckerberg.api

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface.AuthService
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface.LigaService
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface.UserService
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.interfaz.SportMonksService
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.network.AuthInterceptor
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.AuthRepository
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    const val BASE_URL = "http://192.168.1.41:3000/"

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
    // Retrofit para Liga
    val ligaService: LigaService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(LigaService::class.java)
    }
    val userService: UserService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(UserService::class.java)
    }
    val draftService: DraftService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DraftService::class.java)
    }
}
