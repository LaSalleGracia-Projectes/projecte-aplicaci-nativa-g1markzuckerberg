package com.example.projecte_aplicaci_nativa_g1markzuckerberg.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.MainActivity
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.Notifications
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.network.SocketHandler
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.NotifyUtils
import org.json.JSONObject

class NotificationSocketService : Service() {

    companion object {
        private const val FOREGROUND_ID  = 1
        private const val CHANNEL_ID     = "socket_service_channel"
        private const val CHANNEL_NAME   = "Socket Service"
    }

    override fun onCreate() {
        super.onCreate()
        // 1) Arranca foreground con canal EXCLUSIVO para el servicio
        createServiceChannel()
        startForeground(FOREGROUND_ID, buildForegroundNotification())

        // 2) Saca token e ID y conecta
        val token  = RetrofitClient.authRepository.getToken().orEmpty()
        val userId = RetrofitClient.authRepository.getCurrentUserId()
            ?: run {
                stopSelf()
                return
            }

        SocketHandler.connect(userId, token)
        SocketHandler.onNotification { json ->
            // 3) Llega la notificación, la muestras por NotifyUtils (canal HIGH)
            val notif = Notifications(
                mensaje    = json.optString("mensaje", ""),
                id         = json.optInt("id", 0),
                created_at = json.optString("created_at", ""),
                usuario_id = json.optInt("usuario_id", 0),
                is_global  = json.optBoolean("is_global", false)
            )
            NotifyUtils.show(applicationContext, notif)
        }
    }

    override fun onDestroy() {
        SocketHandler.disconnect()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createServiceChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = getSystemService(NotificationManager::class.java)
            if (nm.getNotificationChannel(CHANNEL_ID) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(
                        CHANNEL_ID,
                        CHANNEL_NAME,
                        NotificationManager.IMPORTANCE_LOW
                    ).apply {
                        description = "Mantiene vivo el socket de notificaciones"
                        setSound(null, null)
                    }
                )
            }
        }
    }

    private fun buildForegroundNotification(): Notification {
        val pi = PendingIntent.getActivity(
            this, 0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Conectando notificaciones…")
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentIntent(pi)
            .setOngoing(true)
            .build()
    }
}