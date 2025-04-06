package com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.ForgotPasswordRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.ForgotPasswordResponse
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.UserLigaResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface UserService {
    @GET("api/v1/user/leagues")
    suspend fun getUserLeagues(): Response<UserLigaResponse>

    @POST("api/v1/user/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequest): Response<ForgotPasswordResponse>

}