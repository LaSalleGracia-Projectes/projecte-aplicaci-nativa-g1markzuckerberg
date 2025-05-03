package com.example.projecte_aplicaci_nativa_g1markzuckerberg.service

import android.util.Log
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.api.RetrofitClient
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.FcmTokenRequest
import com.google.firebase.messaging.FirebaseMessagingService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class FirebaseMessagingServiceApp : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Nuevo token FCM: $token")

        // Subir el token al servidor
        uploadTokenToServer(token)
    }

    private fun uploadTokenToServer(fcmToken: String) {
        if (RetrofitClient.authRepository.isLoggedIn()) {
            GlobalScope.launch(Dispatchers.IO) {
                try {
                    RetrofitClient.notificationsService.pushFcmToken(FcmTokenRequest(fcmToken))
                    Log.d("FCM", "Token FCM subido correctamente")
                } catch (e: Exception) {
                    Log.e("FCM", "Error subiendo token: ${e.localizedMessage}")
                }
            }
        }
    }
}
