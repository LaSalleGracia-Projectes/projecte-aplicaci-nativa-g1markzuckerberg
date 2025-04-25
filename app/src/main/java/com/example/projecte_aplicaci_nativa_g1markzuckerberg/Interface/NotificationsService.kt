package com.example.projecte_aplicaci_nativa_g1markzuckerberg.interface_service   // ⇦ usa tu propio package

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.Notifications
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.NotificationsResponse
import retrofit2.Response
import retrofit2.http.GET

/**
 * Llama a  GET http(s)://<TU-BACKEND>/api/v1/user/notifications
 * (la ruta sale de  userRouter.get('/notifications', …)  →  /api/v1/user/notifications).
 *
 * El backend responde:
 * {
 *   "notifications": [ { …Notificacion… }, … ]
 * }
 * Así que definimos un wrapper.
 */
interface NotificationsService {

    @GET("api/v1/user/notifications")
    suspend fun getUserNotifications(): Response<NotificationsResponse>
}