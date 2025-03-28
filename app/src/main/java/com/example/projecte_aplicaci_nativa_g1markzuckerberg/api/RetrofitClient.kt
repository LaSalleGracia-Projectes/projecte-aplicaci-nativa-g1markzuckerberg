package com.example.projecte_aplicaci_nativa_g1markzuckerberg.api

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.interfaz.SportMonksService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    // Si usas el emulador de Android, localhost es 10.0.2.2
    private const val BASE_URL = "http://10.0.2.2:3000/"

    val service: SportMonksService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SportMonksService::class.java)
    }
}
