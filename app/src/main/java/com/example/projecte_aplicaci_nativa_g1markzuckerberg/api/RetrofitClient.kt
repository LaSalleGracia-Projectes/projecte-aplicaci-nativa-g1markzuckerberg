package com.example.projecte_aplicaci_nativa_g1markzuckerberg.api

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface.AuthService
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface.ContactService
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface.LigaService
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface.PlayerService
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface.TeamService
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface.UserService
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.interface_service.NotificationsService
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.interfaz.SportMonksService
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.TeamRepository
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.network.AuthInterceptor
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.AuthRepository
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.PlayerRepository
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

    val authService: AuthService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AuthService::class.java)
    }

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

    val notificationsService: NotificationsService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(NotificationsService::class.java)
    }

    val contactService: ContactService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ContactService::class.java)
    }

    val playerService: PlayerService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(PlayerService::class.java)
    }

    val playerRepository: PlayerRepository by lazy {
        PlayerRepository(playerService)
    }

    val teamService: TeamService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TeamService::class.java)
    }

    val teamRepository: TeamRepository by lazy {
        TeamRepository(teamService)
    }
}
