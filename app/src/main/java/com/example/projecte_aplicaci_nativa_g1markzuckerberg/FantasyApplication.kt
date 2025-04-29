package com.example.projecte_aplicaci_nativa_g1markzuckerberg

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build

class FantasyApplication : Application() {

    override fun onCreate() {
        super.onCreate()

        /* 🔸 Canal donde el **backend** envía las push FCM  */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                /* id  */      "general",                    // ←  coincide con channelId que mandas en FCM
                /* name*/      "Notificaciones FantasyDraft",
                /* importance*/NotificationManager.IMPORTANCE_HIGH   // heads-up
            ).apply { description = "Avisos push (liga, expulsiones…)" }

            getSystemService(NotificationManager::class.java)
                .createNotificationChannel(channel)
        }

        /* 🔹 (opcional) canal para tu servicio de socket,
              en caso de que lo quieras separado: */
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