package com.example.projecte_aplicaci_nativa_g1markzuckerberg.interface_service   // ⇦ usa tu propio package

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.FcmTokenRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.FcmTokenResponse
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.Notifications
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.NotificationsResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path


interface NotificationsService {

    @GET("api/v1/user/notifications")
    suspend fun getUserNotifications(): Response<NotificationsResponse>

    // 2) Guardar/actualizar mi token FCM
    @POST("api/v1/user/fcm-token")
    suspend fun pushFcmToken(
        @Body body: FcmTokenRequest
    ): Response<Unit>

    // 3) Recuperar el token FCM de un usuario por su ID
    @GET("api/v1/user/fcm-token/{id}")
    suspend fun getFcmToken(
        @Path("id") userId: Int
    ): Response<FcmTokenResponse>

}