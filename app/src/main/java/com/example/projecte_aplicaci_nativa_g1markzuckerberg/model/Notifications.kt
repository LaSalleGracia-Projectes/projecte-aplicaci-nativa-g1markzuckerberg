package com.example.projecte_aplicaci_nativa_g1markzuckerberg.model

data class Notifications(
    val mensaje: String,
    val id: Int,
    val created_at: String,
    val usuario_id: Int,
    val is_global: Boolean
)

/** Wrapper que refleja el JSON del backend */
data class NotificationsResponse(
    val notifications: List<Notifications>
)

data class FcmTokenRequest(
    val fcmToken: String
)

// Para el GET /fcm-token/{id}
data class FcmTokenResponse(
    val fcmToken: String
)
