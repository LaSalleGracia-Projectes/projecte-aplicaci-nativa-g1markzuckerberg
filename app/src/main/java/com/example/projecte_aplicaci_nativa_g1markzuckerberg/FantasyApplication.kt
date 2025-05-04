package com.example.projecte_aplicaci_nativa_g1markzuckerberg

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.repository.AuthRepository
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.TokenManager

class FantasyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        // ────────────────────────────────
        // 1. Inicializar TokenManager
        // ────────────────────────────────
        val tokenManager = TokenManager(applicationContext)

        // ────────────────────────────────
        // 2. Inicializar AuthRepository
        // ────────────────────────────────
        RetrofitClient.authRepository = AuthRepository(
            service      = RetrofitClient.authService,
            tokenManager = tokenManager
        )

        // ────────────────────────────────
        // 3. Canal “general” para FCM
        // ────────────────────────────────
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                /* id        */ "general",
                /* name      */ "Notificaciones FantasyDraft",
                /* importance*/ NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Avisos push: jornadas, ligas, expulsiones…"
            }
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }

        // ────────────────────────────────
        // 4. Canal para servicio de socket (opcional)
        // ────────────────────────────────
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                "socket_service_channel",
                "Socket Service",
                NotificationManager.IMPORTANCE_LOW
            )
            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(serviceChannel)
        }
    }
}
