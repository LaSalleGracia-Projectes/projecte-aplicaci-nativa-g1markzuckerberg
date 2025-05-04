package com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.MainActivity
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.R
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.Notifications

object NotifyUtils {

    private const val HEADS_UP_CHANNEL_ID = "real_time_notifications"

    fun show(ctx: Context, notif: Notifications) {
        // 1) Permiso Android 13+ (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ctx.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                != android.content.pm.PackageManager.PERMISSION_GRANTED
            ) return
        }

        // 2) Asegurar canal heads-up
        ensureHeadsUpChannel(ctx)

        // 3) Intent para abrir app al tocar notificación
        val tapIntent = Intent(ctx, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingTap = PendingIntent.getActivity(
            ctx, notif.id, tapIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 4) Construir y lanzar notificación
        val builder = NotificationCompat.Builder(ctx, HEADS_UP_CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notifications)
            .setContentTitle(ctx.getString(R.string.app_name))
            .setContentText(notif.mensaje)
            .setStyle(NotificationCompat.BigTextStyle().bigText(notif.mensaje))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setAutoCancel(true)
            .setContentIntent(pendingTap)
            .setFullScreenIntent(pendingTap, true)
            .setCategory(NotificationCompat.CATEGORY_MESSAGE)

        NotificationManagerCompat.from(ctx)
            .notify(notif.id, builder.build())
    }

    private fun ensureHeadsUpChannel(ctx: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val nm = ctx.getSystemService(NotificationManager::class.java)
            if (nm.getNotificationChannel(HEADS_UP_CHANNEL_ID) == null) {
                nm.createNotificationChannel(
                    NotificationChannel(
                        HEADS_UP_CHANNEL_ID,
                        ctx.getString(R.string.app_name),
                        NotificationManager.IMPORTANCE_HIGH
                    ).apply {
                        description = "Notificaciones en tiempo real (heads-up)"
                        enableLights(true)
                        enableVibration(true)
                    }
                )
            }
        }
    }
}
