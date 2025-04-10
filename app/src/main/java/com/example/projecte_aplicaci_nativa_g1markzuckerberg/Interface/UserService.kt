package com.example.projecte_aplicaci_nativa_g1markzuckerberg.Interface

import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.ForgotPasswordRequest
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.ForgotPasswordResponse
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.UserLigaResponse
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.model.UserMeResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserService {
    @GET("api/v1/user/leagues")
    suspend fun getUserLeagues(): Response<UserLigaResponse>

    @POST("api/v1/user/forgot-password")
    suspend fun forgotPassword(
        @Body request: ForgotPasswordRequest
    ): Response<ForgotPasswordResponse>

    @GET("api/v1/user/get-image")
    suspend fun getUserImage(
        @Query("userId") userId: String
    ): Response<ResponseBody>

    @GET("api/v1/user/me")
    suspend fun getMe(): Response<UserMeResponse>
}